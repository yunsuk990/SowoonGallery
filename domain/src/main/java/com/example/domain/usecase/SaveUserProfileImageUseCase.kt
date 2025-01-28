package com.example.domain.usecase

import android.net.Uri
import com.example.domain.model.DomainUser
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SaveUserProfileImageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(uid: String, uri: Uri?, currentUser: DomainUser, name: String, age: Int) = firebaseRepository.uploadProfileImage(uid,uri, currentUser, name, age)
}