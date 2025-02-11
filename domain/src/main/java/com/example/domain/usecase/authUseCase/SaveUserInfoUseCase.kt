package com.example.domain.usecase.authUseCase

import com.example.domain.model.DomainUser
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SaveUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(user: DomainUser) = authRepository.saveUserInfo(user)
}