package com.yschoi.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yschoi.domain.model.*
import com.yschoi.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import com.yschoi.domain.usecase.authUseCase.GetUserInfoUseCase
import com.yschoi.domain.usecase.chatUseCase.ChatUseCases
import com.yschoi.domain.usecase.chatUseCase.ExitChatRoomUseCase
import com.yschoi.domain.usecase.chatUseCase.ObserveChatRoomUseCase
import com.yschoi.domain.usecase.chatUseCase.SendFCMMessageUseCase
import com.yschoi.domain.usecase.chatUseCase.SetArtworkStateUseCase
import com.yschoi.domain.usecase.chatUseCase.SetUserPresenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatUsecases: ChatUseCases,
    private val getUserUid: GetCurrentUserUidUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val observeChatRoomUseCase: ObserveChatRoomUseCase,
    private val setArtworkStateUseCase: SetArtworkStateUseCase,
    private val sendFCMMessageUseCase: SendFCMMessageUseCase,
    private val setUserPresenceUseCase: SetUserPresenceUseCase,
    private val exitChatRoomUseCase: ExitChatRoomUseCase
): ViewModel() {

    //채팅방 유효성 검사
    private val _chatRoom = MutableStateFlow<DomainChatRoom?>(null)
    val chatRoom: StateFlow<DomainChatRoom?> = _chatRoom.asStateFlow()

    //메세지 리스트
    private val _messageList = MutableStateFlow<List<DomainMessage>>(emptyList())
    val messageList: StateFlow<List<DomainMessage>> = _messageList.asStateFlow()

    //사용자 uid
    private val _currentUserUid = MutableStateFlow<String>("")
    val currentUserUid: StateFlow<String> = _currentUserUid.asStateFlow()

    //현재 사용자 정보
    private val _currentUserInfo = MutableStateFlow<DomainUser?>(null)
    val currentUserInfo: StateFlow<DomainUser?> = _currentUserInfo

    //채팅방 나가기
    private val _exitRoomInfo = MutableStateFlow<Boolean>(false)
    val exitRoomInfo: StateFlow<Boolean> = _exitRoomInfo

    init {
        getUserUid.execute()?.let {
            _currentUserUid.value = it
        }

        viewModelScope.launch {
            _currentUserInfo.value = getUserInfoUseCase.excuteOnce(_currentUserUid.value)
        }

    }

    //채팅방 삭제
    fun exitChatRoom(){
        viewModelScope.launch {
            var response = exitChatRoomUseCase.execute(roomId = chatRoom.value?.roomId!! ,uid = currentUserUid.value)
            when(response){
                is Response.Success -> {
                    _chatRoom.value = null
                    _exitRoomInfo.value = true
                }
                is Response.Error -> {
                    _exitRoomInfo.value = false
                }
            }
        }
    }

    fun setArtworkState(artistUid: String?, sold: Boolean, artworkId: String, destUid: String){
        artistUid?.let {
            setArtworkStateUseCase.execute(artistUid = artistUid, sold = sold, artworkId = artworkId, destUid = destUid)
        }
    }

    // 채팅방 유무 검사
    fun checkChatRoom(userUid: String, artistUid: String, artworkId: String) {
        viewModelScope.launch {
            var response = chatUsecases.checkChatRoom.execute(userUid, artistUid, artworkId)
            when(response){
                is Response.Success -> {
                    if(response.data != null) {
                        _chatRoom.value = response.data
                        observeChatRoom(response.data!!.roomId)  // 실시간 감지 시작
                        loadMessages(response.data!!)
                        enterChatRoom(response.data!!.roomId)
                    }else{
                        _chatRoom.value = null
                    }
                }
                else -> { _chatRoom.value = null }
            }
        }
    }

    // 채팅방 접속 FALSE
    fun leaveChatRoom(c: String){
        viewModelScope.launch {
            setUserPresenceUseCase.leaveChatRoom(roomId = chatRoom.value!!.roomId, uid = currentUserUid.value)
        }
    }

    // 채팅방 접속 ON
    fun enterChatRoom(roomId: String){
        viewModelScope.launch {
            setUserPresenceUseCase.enterChatRoom(roomId = roomId, uid = currentUserUid.value)
        }
    }

    //메세지 리스트 관찰
    fun loadMessages(chatRoom: DomainChatRoom, uid: String = currentUserUid.value){
        viewModelScope.launch {
            chatUsecases.loadMessage.execute(chatRoomId = chatRoom.roomId, uid = uid)
                .collect{ messageList ->
                    _messageList.value = messageList
                }
        }
    }

    // 메세지 전송
    fun sendMessage(message: String, artworkId: String, destUser: DomainUser, artwork: DomainArtwork){
        var messageModel = DomainMessage(
            message = message,
            senderUid = currentUserUid.value,
            timestamp = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date()),
        )
        val notificationModel = NotificationModel(
            token = destUser.pushToken,
            notification = Notification(
                body = message,
                title = currentUserInfo.value?.name!!
            ),
            artwork = artwork,
            destUser = currentUserInfo.value!!
        )
        viewModelScope.launch {
            if(chatRoom.value != null){
                val updatedUnreadMessages = chatRoom.value!!.unreadMessages.toMutableMap().apply {
                    this[destUser.uid] = (this[destUser.uid] ?: 0) + 1  // 상대방 읽지 않은 메시지 증가
                }
                _chatRoom.value?.unreadMessages = updatedUnreadMessages
                chatUsecases.sendMessage.execute(chatRoom.value!!, messageModel)

                //sendFcm
                if(chatRoom.value?.status?.get(destUser.uid) == false){
                    sendFCMMessageUseCase.execute(notificationModel.copy(chatRoom = chatRoom.value!!))
                }
            }else{
                var chatRoom = DomainChatRoom(
                    artworkId = artworkId,
                    users = mapOf(currentUserUid.value to true,  destUser.uid to true),
                    status = mapOf(currentUserUid.value to true, destUser.uid to false),
                    createdAt = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date()),
                    lastMessage = messageModel,
                    unreadMessages = mutableMapOf(
                        destUser.uid to 1,
                        currentUserUid.value to 0
                    )
                )
                //채팅방 새로 만들고 메세지 보내기
                val response = if(currentUserUid.value == artwork.artistUid){
                    chatUsecases.createChatRoom.execute(uid = destUser.uid, destUid = currentUserUid.value, chatRoom = chatRoom)
                }else{
                    chatUsecases.createChatRoom.execute(uid = currentUserUid.value, destUid = destUser.uid, chatRoom = chatRoom)
                }
                when(response){
                    is Response.Success -> {
                        observeChatRoom(response.data.roomId) // 실시간 감지 시작
                        loadMessages(response.data)
                        sendFCMMessageUseCase.execute(notificationModel.copy(chatRoom = response.data))
                    }
                    is Response.Error -> { _chatRoom.value = null}
                }
            }
        }
    }

    fun observeChatRoom(roomId: String){
        viewModelScope.launch {
            observeChatRoomUseCase.execute(roomId).collect { updatedChatRoom ->
                _chatRoom.value = updatedChatRoom
            }
        }
    }
}