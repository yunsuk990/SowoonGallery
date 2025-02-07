package com.example.domain.usecase

import android.net.Uri
import com.example.domain.model.DomainUser
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SaveUserProfileImageUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(uri: Uri?, currentUser: DomainUser, updateUser: DomainUser) = authRepository.updateProfileInfo(uri, currentUser, updateUser)
}