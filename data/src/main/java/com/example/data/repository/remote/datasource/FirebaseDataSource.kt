package com.example.data.repository.remote.datasource

import android.net.Uri
import com.example.domain.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
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

    suspend fun observeChatRoom(chatRoomId: String): Flow<DomainChatRoom>
}