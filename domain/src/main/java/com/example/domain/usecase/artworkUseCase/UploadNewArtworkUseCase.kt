package com.example.domain.usecase.artworkUseCase

import android.net.Uri
import com.example.domain.model.DomainArtwork
import com.example.domain.repository.ArtworkRepository
import javax.inject.Inject

class UploadNewArtworkUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(artwork: DomainArtwork, imageUri: Uri) = artworkRepository.uploadNewArtwork(artwork, imageUri)
    suspend fun executeList(artworkList: List<Pair<Uri, DomainArtwork>>) = artworkRepository.uploadNewArtwork(artworkList)

}