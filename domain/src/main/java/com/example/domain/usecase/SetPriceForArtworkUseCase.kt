package com.example.domain.usecase

import android.util.Log
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SetPriceForArtworkUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(
        category: String,
        artworkId: String,
        price: Float,
        userId: String,
    ): Result<Boolean>{
        return try {
            val task = firebaseRepository.savePriceForArtwork(category, artworkId, price, userId)
            task.await()// Firebase 작업이 끝날 때까지 대기
            Result.success(true) // 성공
        } catch (e: Exception) {
            Result.failure(e) // 예외 처리
        }
    }
}