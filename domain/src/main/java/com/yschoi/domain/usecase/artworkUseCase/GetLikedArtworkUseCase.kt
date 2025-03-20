package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetLikedArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    fun execute(uid: String, artworkUid: String, callback: (Boolean) -> Unit) {
        artworkRepository.getLikedArtwork(uid, artworkUid).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    callback(true)
                } else {
                    callback(false)
                }
            }else{
                callback(false)
            }
        }
    }
}