package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class SetFavoriteArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {

    fun execute(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
        callback: (Boolean) -> Unit
    ){
        artworkRepository.setFavoriteArtwork(uid,artworkUid,isFavorite).addOnCompleteListener{ task ->
            if(task.isSuccessful) callback(isFavorite)
            else callback(false)
        }

    }

}