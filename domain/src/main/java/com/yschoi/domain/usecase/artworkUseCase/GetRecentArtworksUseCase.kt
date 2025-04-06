package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetRecentArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(
        limit: Int
    ): Flow<List<DomainArtwork>> = artworkRepository.getRecentArtworks(limit)
}