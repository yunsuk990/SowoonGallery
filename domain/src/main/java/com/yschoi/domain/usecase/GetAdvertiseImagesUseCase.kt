package com.yschoi.domain.usecase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetAdvertiseImagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute() = firebaseRepository.getAdvertiseImages()
}