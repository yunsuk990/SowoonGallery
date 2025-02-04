package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class CheckChatRoomUserCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(uid: String, destUid: String, artworkId: String) = firebaseRepository.checkChatRoom(uid = uid, destUid = destUid, artworkId = artworkId)
}