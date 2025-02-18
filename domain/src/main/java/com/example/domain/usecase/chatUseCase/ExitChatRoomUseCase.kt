package com.example.domain.usecase.chatUseCase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class ExitChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(artistUid: String, sold: Boolean, artworkId: String, destUid: String) = firebaseRepository.exitChatRoom(artistUid, sold, artworkId, destUid)
}