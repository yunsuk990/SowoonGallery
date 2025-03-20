package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class LoadMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(chatRoomId: String, uid: String) = firebaseRepository.loadMessage(chatRoomId, uid)

}