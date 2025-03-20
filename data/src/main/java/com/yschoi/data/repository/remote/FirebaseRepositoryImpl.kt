package com.yschoi.data.repository.remote

import com.yschoi.data.repository.remote.datasource.ArtworkDataSource
import com.yschoi.data.repository.remote.datasource.AuthDataSource
import com.yschoi.data.repository.remote.datasource.FirebaseDataSource
import com.yschoi.domain.model.*
import com.yschoi.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val artworkDataSource: ArtworkDataSource,
    private val authDataSource: AuthDataSource
): FirebaseRepository {

    override suspend fun loadMessage(chatroomId: String, uid: String) = firebaseDataSource.loadMessage(chatroomId, uid)
    override suspend fun observeChatRoom(chatroomId: String): Flow<DomainChatRoom> = firebaseDataSource.observeChatRoom(chatroomId)

    override fun setArtworkState(artistUid: String, sold: Boolean, artworkId: String, destUid: String?) {
        artworkDataSource.updateArtworkSoldState(artistUid, artworkId, sold, destUid)
    }

    override suspend fun sendFCMMessage(notificationModel: NotificationModel) = firebaseDataSource.sendFCMMessage(notificationModel)

    override suspend fun leaveChatRoom(roomId: String, uid: String) = firebaseDataSource.leaveChatRoom(roomId, uid)

    override suspend fun enterChatRoom(roomId: String, uid: String) = firebaseDataSource.enterChatRoom(roomId, uid)
    override suspend fun deleteChatRoom(uid: String) = firebaseDataSource.deleteChatRoom(uid)
    override suspend fun exitChatRoom(roomId: String, uid: String): Response<Boolean> = firebaseDataSource.exitChatRoom(roomId, uid)

    override suspend fun observeStatus(roomId: String): Flow<Map<String, Boolean>> = firebaseDataSource.observeStatus(roomId)

    override suspend fun observeUnReadMessages(roomId: String): Flow<Map<String, Int>> = firebaseDataSource.observeUnReadMessages(roomId)

    override suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom) = firebaseDataSource.createChatRoom(uid, destUid, chatRoom)
    //광고 사진
    override suspend fun getAdvertiseImages() = firebaseDataSource.getAdvertiseImages()
    override suspend fun getUserChatRoomLists(uid: String): Flow<List<DomainChatRoomWithUser>> = firebaseDataSource.getUserChatRoomLists(uid)
    override suspend fun sendMessage(chatroom: DomainChatRoom, message: DomainMessage): Response<Boolean> = firebaseDataSource.sendMessage(chatroom, message)


    override suspend fun getUserInfoLists(
        uid: List<String>,
    ) = firebaseDataSource.getUserInfoLists(uid)

    override suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<DomainChatRoom?> = firebaseDataSource.checkChatRoom(uid, destUid, artworkId)

}