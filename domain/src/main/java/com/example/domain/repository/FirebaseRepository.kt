package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.stream.Stream

interface FirebaseRepository {

    suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom): Response<DomainChatRoom>

    suspend fun getUserInfoLists(uid: List<String>): Response<List<DomainUser>>

    suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<DomainChatRoom?>
    
    suspend fun getAdvertiseImages(): Response<List<String>>

    //유저 채팅방 목록 불러오기
    suspend fun getUserChatRoomLists(uid: String): Flow<List<DomainChatRoomWithUser>>

    //메세지 보내기
    suspend fun sendMessage(chatroom: DomainChatRoom, message: DomainMessage): Response<Boolean>

    //메시지 가져오기
    suspend fun loadMessage(chatroomId: String, uid: String): Flow<List<DomainMessage>>

    suspend fun observeChatRoom(chatroomId: String): Flow<DomainChatRoom>

    fun exitChatRoom(artistUid: String, sold: Boolean, artworkId: String, destUid: String)
}