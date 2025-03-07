package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class SaveRecentCategoryUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(category: String) = authRepository.saveRecentCategory(category)
}