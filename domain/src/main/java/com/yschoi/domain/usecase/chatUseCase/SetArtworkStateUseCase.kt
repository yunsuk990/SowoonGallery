package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetArtworkStateUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(artistUid: String, sold: Boolean, artworkId: String, destUid: String?) = firebaseRepository.setArtworkState(artistUid, sold, artworkId, destUid)
}
