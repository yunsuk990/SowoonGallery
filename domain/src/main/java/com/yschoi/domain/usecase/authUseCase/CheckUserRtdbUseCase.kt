package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class CheckUserRtdbUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(uid: String) = authRepository.checkUserRtdbUseCase(uid)
}