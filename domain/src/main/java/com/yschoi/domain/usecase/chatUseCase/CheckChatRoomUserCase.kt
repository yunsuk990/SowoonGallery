package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class CheckChatRoomUserCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(uid: String, destUid: String, artworkId: String) = firebaseRepository.checkChatRoom(uid = uid, destUid = destUid, artworkId = artworkId)
}