package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetLikedArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {

    suspend fun execute(uid: String) = artworkRepository.getLikedArtworks(uid)

}