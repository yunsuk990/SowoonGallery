package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.model.DomainChatRoom
import com.yschoi.domain.model.DomainMessage
import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(chatRoom: DomainChatRoom, message: DomainMessage) = firebaseRepository.sendMessage(chatRoom, message)
}