package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    fun execute() = authRepository.getUid()
}
