package com.example.domain.usecase

import com.example.domain.model.DomainUser
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SaveUserInfoUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(user: DomainUser) = firebaseRepository.saveUserInfo(user)
}