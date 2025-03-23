package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

data class FetchArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
){
    suspend fun fetchArtwork(artworkId: String) = artworkRepository.fetchArtwork(artworkId)
}