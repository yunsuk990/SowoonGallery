package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(uid: String): Flow<List<DomainArtwork>> {
        return artworkRepository.getFavoriteArtworks(uid)
    }
}