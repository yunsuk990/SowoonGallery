package com.example.domain.usecase.artworkUseCase

import android.util.Log
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetLikedArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    fun execute(uid: String, artworkUid: String, callback: (Boolean) -> Unit) {
        artworkRepository.getLikedArtwork(uid, artworkUid).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    Log.d("getLikedArtwork ${artworkUid}", "true")
                    callback(true)
                } else {
                    Log.d("getLikedArtwork ${artworkUid}", "false")
                    callback(false)
                }
            }else{
                callback(false)
            }
        }
    }
}