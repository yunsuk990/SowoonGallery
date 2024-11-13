package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetFavoriteArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    fun execute(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
        category: String,
        callback: (Boolean) -> Unit
    ){
        firebaseRepository.setFavoriteArtwork(uid,artworkUid,isFavorite,category).addOnCompleteListener{ task ->
            if(task.isSuccessful) callback(isFavorite)
            else callback(false)
        }

    }

}