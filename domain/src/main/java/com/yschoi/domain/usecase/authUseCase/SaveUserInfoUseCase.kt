package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class SaveUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(user: DomainUser) = authRepository.saveUserInfo(user)
}