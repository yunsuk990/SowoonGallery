package com.example.domain.usecase

import com.example.domain.model.Response
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SignInWithPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(credential: PhoneAuthCredential): Response<String?> {
        return authRepository.signInWithPhoneAuthCredential(credential)
    }
}