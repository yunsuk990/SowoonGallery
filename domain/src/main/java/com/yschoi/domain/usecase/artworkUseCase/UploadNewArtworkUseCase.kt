package com.yschoi.domain.usecase.artworkUseCase

import android.net.Uri
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.repository.ArtworkRepository
import javax.inject.Inject

class UploadNewArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun executeList(artworkList: List<Pair<Uri, DomainArtwork>>) = artworkRepository.uploadNewArtwork(artworkList)

}