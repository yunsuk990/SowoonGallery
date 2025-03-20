package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetRecentArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(limit: Int): List<DomainArtwork> = artworkRepository.getRecentArtworks(limit)

}