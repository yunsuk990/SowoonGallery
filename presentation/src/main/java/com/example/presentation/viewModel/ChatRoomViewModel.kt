package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.*
import com.example.domain.usecase.artworkUseCase.GetArtworkByIdUseCase
import com.example.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import com.example.domain.usecase.authUseCase.GetUserInfoUseCase
import com.example.domain.usecase.authUseCase.SaveUserInfoUseCase
import com.example.domain.usecase.chatUseCase.ChatUseCases
import com.example.presentation.model.ChatState
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
    private val getUserInfoUseCase: GetUserInfoUseCase
): ViewModel() {

    //채팅방 유효성 검사
    private val _checkChatState: MutableStateFlow<ChatState> = MutableStateFlow(ChatState.Idle)
    val checkChatState: StateFlow<ChatState> = _checkChatState.asStateFlow()

    //메세지 리스트
    private val _messageList = MutableStateFlow<List<DomainMessage>>(emptyList())
    val messageList: StateFlow<List<DomainMessage>> = _messageList.asStateFlow()

    //메세지 리스트
    private val _currentUserUid = MutableStateFlow<String>("")
    val currentUserUid: StateFlow<String> = _currentUserUid.asStateFlow()

    init {
        getUserUid.execute()?.let {
            _currentUserUid.value = it
        }
    }

    fun checkChatRoom(destUid: String, artworkId: String) {
        viewModelScope.launch {
            _checkChatState.value = ChatState.Loading
            Log.d("checkChatRoom_uid", currentUserUid.value)
            var response = chatUsecases.checkChatRoom.execute(currentUserUid.value, destUid, artworkId)
            Log.d("checkChatRoom_ChatRoomViewModel", response.toString())
            when(response){
                is Response.Success -> {
                    response.data?.let {
                        _checkChatState.value = ChatState.OldChat(it)
                        loadMessages(it)
                    } ?: run {
                        _checkChatState.value = ChatState.NewChat
                    }
                }
                is Response.Error -> { _checkChatState.value = ChatState.Error(response.message) }
                else -> _checkChatState.value = ChatState.Idle
            }
            Log.d("checkChatRoom_ChatRoomViewModel", checkChatState.value.toString())
        }
    }

    fun loadMessages(chatRoomId: String){
        viewModelScope.launch {
            chatUsecases.loadMessage.execute(chatRoomId = chatRoomId)
                .collect{ messageList ->
                    _messageList.value = messageList
                }
        }
    }


    fun sendMessage(message: String, opponentUid: String, artworkId: String){
        Log.d("sendMessage", checkChatState.value.toString())
        var messageModel = DomainMessage(
            message = message,
            senderUid = currentUserUid.value,
            timestamp = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date())
        )
        viewModelScope.launch {
            when(val state = _checkChatState.value){
                is ChatState.NewChat -> {
                    var chatModel = DomainChatRoom(
                        artworkId = artworkId,
                        users = mapOf(currentUserUid.value to true,  opponentUid to true),
                        createdAt = SimpleDateFormat("yyyy.MM.dd/HH:mm").format(Date()),
                        lastMessage = messageModel
                    )
                    //채팅방 새로 만들고 메세지 보내기
                    val response = chatUsecases.createChatRoom.execute(uid = currentUserUid.value, destUid = opponentUid, chatRoom = chatModel)
                    when(response){
                        is Response.Success -> {
                            var chatRoomId = response.data
                            _checkChatState.value = ChatState.OldChat(chatRoomId)
                            loadMessages(chatRoomId)
                            //chatUsecases.sendMessage.execute(chatRoomId, messageModel)
                        }
                        is Response.Error -> { _checkChatState.value = ChatState.Error(response.message)}
                    }
                }
                is ChatState.OldChat -> {
                    chatUsecases.sendMessage.execute(state.value, messageModel)
                }
                else -> Unit
            }
        }
    }
}