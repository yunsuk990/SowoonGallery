package com.example.domain.usecase.chatUseCase

import javax.inject.Inject

data class ChatUseCases @Inject constructor(
    val sendMessage: SendMessageUseCase,
    val loadMessage: LoadMessageUseCase,
    val createChatRoom: CreateChatRoomUseCase,
    val getChatLists: GetUserChatListsUseCase,
    val checkChatRoom: CheckChatRoomUserCase
)
