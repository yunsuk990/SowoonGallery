package com.example.data.repository.remote

import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.data.repository.remote.datasource.AuthDataSource
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.*
import com.example.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val artworkDataSource: ArtworkDataSource,
    private val authDataSource: AuthDataSource
): FirebaseRepository {

    override suspend fun loadMessage(chatroomId: String, uid: String) = firebaseDataSource.loadMessage(chatroomId, uid)
    override suspend fun observeChatRoom(chatroomId: String): Flow<DomainChatRoom> = firebaseDataSource.observeChatRoom(chatroomId)

    override fun exitChatRoom(artistUid: String, sold: Boolean, artworkId: String, destUid: String) {
        artworkDataSource.updateArtworkSoldState(artistUid, artworkId, sold, destUid)
    }

    override suspend fun sendFCMMessage(notificationModel: NotificationModel) = firebaseDataSource.sendFCMMessage(notificationModel)

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