package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class SetLikedArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {

    fun execute(
        uid: String,
        artworkUid: String,
        isLiked: Boolean,
        callback: (Boolean) -> Unit
    ){
        artworkRepository.setLikedArtwork(uid,artworkUid, isLiked).addOnCompleteListener{ task ->
            if(task.isSuccessful) callback(isLiked)
            else callback(false)
        }
    }
}