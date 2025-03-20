package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class GetLikedCountArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    fun execute(artworkUid: String, callback: (Int) -> Unit){
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                callback(count)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(0)
            }
        }
        artworkRepository.getLikedCountArtwork(artworkUid,listener)
    }

}