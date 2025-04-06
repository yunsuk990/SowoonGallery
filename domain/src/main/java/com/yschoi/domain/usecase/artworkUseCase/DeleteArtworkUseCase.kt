package com.yschoi.domain.usecase.artworkUseCase

import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(
        artworkId: String,
        uid: String,
        category: String,
        imageUrl: String
    ) = artworkRepository.deleteArtwork(artworkId, uid, category, imageUrl)
}