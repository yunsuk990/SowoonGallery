package com.example.domain.usecase

import android.util.Log
import com.example.domain.repository.FirebaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class GetLikedCountArtworkUsecase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(artworkUid: String, category: String, callback: (Int) -> Unit){
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
        firebaseRepository.getLikedCountArtwork(artworkUid, category,listener)
    }

}