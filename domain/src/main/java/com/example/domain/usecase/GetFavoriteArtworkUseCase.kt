package com.example.domain.usecase

import android.util.Log
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetFavoriteArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    fun execute(uid: String, artworkUid: String, callback: (Boolean) -> Unit) {
        firebaseRepository.getFavoriteArtwork(uid, artworkUid).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    Log.d("getFavoriteArtwork ${artworkUid}", "true")
                    callback(true)
                } else {
                    Log.d("getFavoriteArtwork ${artworkUid}", "false")
                    callback(false)
                }
            }else{
                callback(false)
            }
        }
    }
}