package com.example.domain.usecase.artworkUseCase

import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetLikedArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {

    suspend fun execute(uid: String) = artworkRepository.getLikedArtworks(uid)

}