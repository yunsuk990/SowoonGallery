package com.yschoi.domain.usecase.chatUseCase

import com.yschoi.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetUserChatListsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend fun execute(uid: String) = firebaseRepository.getUserChatRoomLists(uid)

}