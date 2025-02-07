package com.example.domain.usecase.chatUseCase

import com.example.domain.model.DomainChatRoom
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class CreateChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(uid: String, destUid: String, chatRoom: DomainChatRoom) = firebaseRepository.createChatRoom(uid,destUid,chatRoom)
}