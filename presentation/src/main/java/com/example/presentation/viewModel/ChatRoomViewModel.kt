package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.*
import com.example.domain.usecase.artworkUseCase.GetArtworkByIdUseCase
import com.example.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import com.example.domain.usecase.authUseCase.GetUserInfoUseCase
import com.example.domain.usecase.chatUseCase.ChatUseCases
import com.example.domain.usecase.chatUseCase.ExitChatRoomUseCase
import com.example.domain.usecase.chatUseCase.ObserveChatRoomUseCase
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
    private val getArtworkByIdUseCase: GetArtworkByIdUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val observeChatRoomUseCase: ObserveChatRoomUseCase,
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
    private val _currentUserInfo = MutableStateFlow<DomainUser>(DomainUser())
    val currentUserInfo: StateFlow<DomainUser> = _currentUserInfo


    init {
        getUserUid.execute()?.let {
            _currentUserUid.value = it
        }

        viewModelScope.launch {
            _currentUserInfo.value = getUserInfoUseCase.excuteOnce(_currentUserUid.value)
        }

    }

    fun exitChatRoom(artistUid: String, sold: Boolean, artworkId: String, destUid: String){
        artistUid?.let {
            exitChatRoomUseCase.execute(artistUid = artistUid, sold = sold, artworkId = artworkId, destUid = destUid)
        }
    }

    fun checkChatRoom(destUid: String, artworkId: String) {
        viewModelScope.launch {
            var response = chatUsecases.checkChatRoom.execute(currentUserUid.value, destUid, artworkId)
            when(response){
                is Response.Success -> {
                    if(response.data != null) {
                        observeChatRoom(response.data!!.roomId)  // 실시간 감지 시작
                        loadMessages(response.data!!)
                    }else{
                        _chatRoom.value = null
                    }
                }
                else -> { _chatRoom.value = null }
            }
        }
    }

    fun loadMessages(chatRoom: DomainChatRoom, uid: String = currentUserUid.value){
        viewModelScope.launch {
            chatUsecases.loadMessage.execute(chatRoomId = chatRoom.roomId, uid = uid)
                .collect{ messageList ->
                    _messageList.value = messageList
                }
        }
    }


    fun sendMessage(message: String, opponentUid: String, artworkId: String){
        var messageModel = DomainMessage(
            message = message,
            senderUid = currentUserUid.value,
            timestamp = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date()),
        )
        viewModelScope.launch {
            if(chatRoom.value != null){
                val updatedUnreadMessages = chatRoom.value!!.unreadMessages.toMutableMap().apply {
                    this[opponentUid] = (this[opponentUid] ?: 0) + 1  // 상대방 읽지 않은 메시지 증가
                }
                _chatRoom.value?.unreadMessages = updatedUnreadMessages
                chatUsecases.sendMessage.execute(chatRoom.value!!, messageModel)
            }else{
                var chatModel = DomainChatRoom(
                    artworkId = artworkId,
                    users = mapOf(currentUserUid.value to true,  opponentUid to true),
                    createdAt = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date()),
                    lastMessage = messageModel,
                    unreadMessages = mutableMapOf(
                        opponentUid to 1,
                        currentUserUid.value to 0
                    )
                )
                //채팅방 새로 만들고 메세지 보내기
                val response = chatUsecases.createChatRoom.execute(uid = currentUserUid.value, destUid = opponentUid, chatRoom = chatModel)
                when(response){
                    is Response.Success -> {
                        observeChatRoom(response.data!!.roomId) // 실시간 감지 시작
                        loadMessages(response.data!!)
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
                Log.d("observeChatRoom", updatedChatRoom.toString())
            }
        }
    }
}