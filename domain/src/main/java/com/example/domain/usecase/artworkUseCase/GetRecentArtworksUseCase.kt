package com.example.domain.usecase.artworkUseCase

import com.example.domain.model.DomainArtwork
import com.example.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetRecentArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(limit: Int): List<DomainArtwork> = artworkRepository.getRecentArtworks(limit)

}