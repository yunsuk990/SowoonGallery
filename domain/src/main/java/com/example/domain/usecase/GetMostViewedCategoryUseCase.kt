package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class GetMostViewedCategoryUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute() = authRepository.getMostViewedCategory()
}