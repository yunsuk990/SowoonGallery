package com.example.domain.usecase.authUseCase

import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(uid: String): Flow<DomainUser?> = authRepository.getUserInfo(uid)
}