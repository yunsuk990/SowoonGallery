package com.example.domain.usecase.artworkUseCase

import android.util.Log
import com.example.domain.model.DomainArtwork
import com.example.domain.repository.ArtworkRepository
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArtworksUseCase @Inject constructor(
    private val artworkRepository: ArtworkRepository
) {
    suspend fun execute(): List<DomainArtwork> = artworkRepository.getArtworkLists()

    suspend fun executeByUid(artistUid: String): List<DomainArtwork> = artworkRepository.getArtistArtworks(artistUid)
}