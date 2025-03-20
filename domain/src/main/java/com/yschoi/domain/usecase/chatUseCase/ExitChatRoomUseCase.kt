package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.model.Response
import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class ExitChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    suspend fun execute(roomId: String, uid: String): Response<Boolean> = firebaseRepository.exitChatRoom(roomId, uid)
}