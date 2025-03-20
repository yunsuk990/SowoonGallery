package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetUserPresenceUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    suspend fun leaveChatRoom(roomId: String, uid: String) = firebaseRepository.leaveChatRoom(roomId, uid)
    suspend fun enterChatRoom(roomId: String, uid: String) = firebaseRepository.enterChatRoom(roomId, uid)

}