package com.yschoi.domain.usecase.authUseCase

import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(uid: String): Flow<DomainUser?> = authRepository.getUserInfo(uid)

    suspend fun excuteOnce(uid: String): Flow<DomainUser> = authRepository.getUserInfoOnce(uid)
}