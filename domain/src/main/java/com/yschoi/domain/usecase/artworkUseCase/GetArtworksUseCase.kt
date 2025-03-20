package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(): List<DomainArtwork> = artworkRepository.getArtworkLists()

    suspend fun executeByUid(artistUid: String): List<DomainArtwork> = artworkRepository.getArtistArtworks(artistUid)
}