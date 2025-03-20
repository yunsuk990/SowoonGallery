package com.yschoi.domain.usecase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class SaveRecentCategoryUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute(category: String) = authRepository.saveRecentCategory(category)
}