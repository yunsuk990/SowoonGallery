package com.example.domain.usecase.artworkUseCase

import com.example.domain.model.Career
import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class SetArtistProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun executeArtistIntroduce(artistIntroduce: String) = authRepository.setArtistIntroduce(artistIntroduce)
    suspend fun executeArtistCareer(career: Career) = authRepository.setArtistCareer(career)
}