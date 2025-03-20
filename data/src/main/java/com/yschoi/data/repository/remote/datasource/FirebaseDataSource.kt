package com.yschoi.data.repository.remote.datasource

import com.yschoi.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {

    suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom): Response<DomainChatRoom>

    //광고 사진 불러오기
    suspend fun getAdvertiseImages(): Response<List<String>>

    //메세지 보내기
    suspend fun sendMessage(chatroom: DomainChatRoom, message: DomainMessage): Response<Boolean>

    //메시지 가져오기
    suspend fun loadMessage(chatroomId: String, uid: String): Flow<List<DomainMessage>>

    //채팅방 존재 여부 확인
    suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<DomainChatRoom?>

    suspend fun getUserInfoLists(uid: List<String>): Response<List<DomainUser>>

    //유저 채팅방 목록 불러오기
    suspend fun getUserChatRoomLists(uid: String): Flow<List<DomainChatRoomWithUser>>

    fun markMessageAsRead(chatroomId: String, userUid: String)

    fun getChatRoomId(userId: String, artistId: String, artworkId: String): String

    suspend fun observeChatRoom(chatRoomId: String): Flow<DomainChatRoom>

    suspend fun sendFCMMessage(notificationModel: NotificationModel)

    suspend fun leaveChatRoom(roomId: String, uid: String)

    suspend fun enterChatRoom(roomId: String, uid: String)

    suspend fun deleteChatRoom(uid: String)

    suspend fun exitChatRoom(roomId: String, uid: String): Response<Boolean>

    suspend fun observeStatus(roomId: String): Flow<Map<String, Boolean>>
    suspend fun observeUnReadMessages(roomId: String): Flow<Map<String, Int>>

}