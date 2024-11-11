package com.example.domain.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetFavoriteArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    fun execute(uid: String, artworkUid: String): LiveData<Boolean> {
        val isFavorite: MutableLiveData<Boolean> = MutableLiveData()
        firebaseRepository.getFavoriteArtwork(uid, artworkUid).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    Log.d("getFavoriteArtwork ${artworkUid}", "true")
                    isFavorite.value = true
                } else {
                    Log.d("getFavoriteArtwork ${artworkUid}", "false")
                    isFavorite.value = false
                }
            }
        }
        return isFavorite
    }


}