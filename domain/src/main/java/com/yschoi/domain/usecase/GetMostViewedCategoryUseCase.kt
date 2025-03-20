package com.yschoi.domain.usecase

import com.yschoi.domain.repository.AuthRepository
import javax.inject.Inject

class GetMostViewedCategoryUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun execute() = authRepository.getMostViewedCategory()
}