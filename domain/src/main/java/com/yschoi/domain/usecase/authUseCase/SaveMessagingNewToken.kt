package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class SaveMessagingNewToken @Inject constructor(
    private val authRepository: AuthRepository
){
    fun execute(token: String) = authRepository.registerMessagingNewToken(token = token)
}