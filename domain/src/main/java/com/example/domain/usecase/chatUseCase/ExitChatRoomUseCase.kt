package com.example.domain.usecase.chatUseCase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class ExitChatRoomUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(sold: Boolean, artworkId: String) = firebaseRepository.exitChatRoom(sold, artworkId)
}