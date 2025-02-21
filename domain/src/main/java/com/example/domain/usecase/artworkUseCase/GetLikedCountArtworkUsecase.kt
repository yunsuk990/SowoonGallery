package com.example.domain.usecase.artworkUseCase

import android.util.Log
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
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
                Log.d("GetLiked",  count.toString())
                callback(count)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(0)
            }
        }
        artworkRepository.getLikedCountArtwork(artworkUid,listener)
    }

}