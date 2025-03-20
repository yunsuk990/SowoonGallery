package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class GetArtworkByIdUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
){
    suspend fun execute(artworkId: String) = artworkRepository.getArtworkById(artworkId)
}