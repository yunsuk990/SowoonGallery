package com.example.domain.usecase.chatUseCase

import com.example.domain.model.NotificationModel
import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendFCMMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(notificationModel: NotificationModel) = firebaseRepository.sendFCMMessage(notificationModel)

}