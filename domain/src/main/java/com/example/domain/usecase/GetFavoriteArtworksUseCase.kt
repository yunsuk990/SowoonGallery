package com.example.domain.usecase

import android.util.Log
import com.example.domain.model.DomainArtwork
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteArtworksUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(uid: String): Flow<List<DomainArtwork>> {
        return firebaseRepository.getFavoriteArtworks(uid)
    }
}