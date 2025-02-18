package com.example.domain.usecase.chatUseCase

import android.util.Log
import com.example.domain.model.DomainChatRoom
import com.example.domain.model.DomainChatRoomWithUser
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetUserChatListsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend fun execute(uid: String) = firebaseRepository.getUserChatRoomLists(uid)

}