package com.example.domain.usecase.authUseCase

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(uid: String) = authRepository.deleteUserAccount(uid)
}