package com.example.domain.usecase.artworkUseCase

import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
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