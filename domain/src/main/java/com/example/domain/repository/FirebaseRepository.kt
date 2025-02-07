package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom): Response<String>

    suspend fun getUserInfoLists(uid: List<String>): Response<List<DomainUser>>

    suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<String?>
    
    suspend fun getAdvertiseImages(): Response<List<String>>

    //유저 채팅방 목록 불러오기
    suspend fun getUserChatRoomLists(uid: String): Flow<List<DomainChatRoomWithUser>>

    //메세지 보내기
    suspend fun sendMessage(chatroomId: String, message: DomainMessage): Response<Boolean>

    //메시지 가져오기
    suspend fun loadMessage(chatroomId: String): Flow<List<DomainMessage>>

}