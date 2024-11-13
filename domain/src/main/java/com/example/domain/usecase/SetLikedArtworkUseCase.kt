package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetLikedArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    fun execute(
        uid: String,
        artworkUid: String,
        isLiked: Boolean,
        category: String,
        callback: (Boolean) -> Unit
    ){
        firebaseRepository.setLikedArtwork(uid,artworkUid, isLiked, category).addOnCompleteListener{ task ->
            if(task.isSuccessful) callback(isLiked)
            else callback(false)
        }
    }
}