package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetAdvertiseImagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute() = firebaseRepository.getAdvertiseImages()
}