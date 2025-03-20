package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class ObserveChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(roomId: String) = firebaseRepository.observeChatRoom(roomId)
}