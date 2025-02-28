package com.example.domain.usecase.authUseCase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class SaveMessagingToken @Inject constructor(
    private val authRepository: AuthRepository
){
    fun execute(uid: String) = authRepository.registerMessagingToken(uid)
}