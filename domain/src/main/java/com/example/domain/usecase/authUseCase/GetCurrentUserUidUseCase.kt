package com.example.domain.usecase.authUseCase

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    fun execute() = authRepository.getUid()
}
