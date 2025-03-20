package com.yschoi.domain.usecase

import android.net.Uri
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class SaveUserProfileImageUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(uri: Uri?, currentUser: DomainUser, updateUser: DomainUser) = authRepository.updateProfileInfo(uri, currentUser, updateUser)
}