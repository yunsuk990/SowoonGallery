package com.example.domain.usecase

import com.example.domain.repository.FirebaseRepository
import javax.inject.Inject

class GetUserChatListsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend fun execute(uid: String) = firebaseRepository.getUserChatRoomLists(uid)

}