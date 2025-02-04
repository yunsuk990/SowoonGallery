package com.example.domain.usecase.artworkUseCase

import com.example.domain.model.DomainArtwork
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(uid: String): Flow<List<DomainArtwork>> {
        return artworkRepository.getFavoriteArtworks(uid)
    }
}