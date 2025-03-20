package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetArtistSoldArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(artworksUid: Map<String, Boolean>) = artworkRepository.getArtistSoldArtworks(artworksUid)
}