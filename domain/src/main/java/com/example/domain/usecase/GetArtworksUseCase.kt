package com.example.domain.usecase

import android.util.Log
import com.example.domain.model.DomainArtwork
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArtworksUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(category: String): Flow<List<DomainArtwork>> = flow {
        val artworks = firebaseRepository.getArtworkLists(category)
        emit(artworks)
    }.catch { e ->
        Log.d("GetArtworksUseCase", e.toString())
        emit(emptyList())
    }
}