package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class CheckUserRtdbUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(uid: String) = firebaseRepository.checkUserRtdbUseCase(uid)
}