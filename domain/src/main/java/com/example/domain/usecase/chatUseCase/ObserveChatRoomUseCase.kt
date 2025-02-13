package com.example.domain.usecase.chatUseCase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class ObserveChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(roomId: String) = firebaseRepository.observeChatRoom(roomId)
}