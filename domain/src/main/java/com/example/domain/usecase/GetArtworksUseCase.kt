package com.example.domain.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.DomainArtwork
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetArtworksUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(category: String?): List<DomainArtwork> {
        return firebaseRepository.getArtworkLists(category)
    }
}