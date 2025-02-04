package com.example.domain.usecase.authUseCase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute() = authRepository.signOut()
}