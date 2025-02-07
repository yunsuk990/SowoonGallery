package com.example.domain.usecase.artworkUseCase

import com.example.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetArtworkByIdUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
){
    suspend fun execute(artworkId: String) = artworkRepository.getArtworkById(artworkId)
}