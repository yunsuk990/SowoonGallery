package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class SaveMessagingToken @Inject constructor(
    private val authRepository: AuthRepository
){
    fun execute(uid: String) = authRepository.registerMessagingToken(uid)
}