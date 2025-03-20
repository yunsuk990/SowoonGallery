package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.model.NotificationModel
import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendFCMMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun execute(notificationModel: NotificationModel){
        firebaseRepository.sendFCMMessage(notificationModel)
    }

}