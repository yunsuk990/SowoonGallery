package com.example.data.repository.remote

import android.net.Uri
import android.util.Log
import com.example.data.mapper.MainMapper
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.*
import com.example.domain.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
): FirebaseRepository {

    override suspend fun loadMessage(chatroomId: String, callback: (List<DomainMessage>) -> Unit)= firebaseDataSource.loadMessage(chatroomId, callback)
    override suspend fun loadMessage(chatroomId: String) = firebaseDataSource.loadMessage(chatroomId)
    override suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom) = firebaseDataSource.createChatRoom(uid, destUid, chatRoom)


    //광고 사진
    override suspend fun getAdvertiseImages() = firebaseDataSource.getAdvertiseImages()
    override suspend fun getUserChatRoomLists(uid: String): Response<List<DomainChatRoomWithUser>> = firebaseDataSource.getUserChatRoomLists(uid)
    override suspend fun sendMessage(chatroomId: String, message: DomainMessage): Response<Boolean> = firebaseDataSource.sendMessage(chatroomId, message)


    override suspend fun getUserInfoLists(
        uid: List<String>,
    ) = firebaseDataSource.getUserInfoLists(uid)

    override suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<String?> = firebaseDataSource.checkChatRoom(uid, destUid, artworkId)

}