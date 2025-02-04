package com.example.domain.usecase.authUseCase

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class CheckUserRtdbUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(uid: String) = authRepository.checkUserRtdbUseCase(uid)
}