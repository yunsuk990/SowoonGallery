package com.yschoi.domain.usecase

import com.yschoi.domain.model.Response
import com.yschoi.domain.repository.AuthRepository
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SignInWithPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(credential: PhoneAuthCredential): Response<String?> {
        return authRepository.signInWithPhoneAuthCredential(credential)
    }
}