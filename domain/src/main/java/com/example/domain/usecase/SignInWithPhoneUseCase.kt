package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SignInWithPhoneUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun excute(credential: PhoneAuthCredential): Task<AuthResult> {
        return firebaseRepository.signInWithPhoneAuthCredential(credential)
    }
}