package com.example.domain.usecase.chatUseCase

import com.example.domain.model.DomainMessage
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class LoadMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(chatRoomId: String) = firebaseRepository.loadMessage(chatRoomId)

}