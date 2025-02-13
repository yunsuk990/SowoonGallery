package com.example.data.repository.remote.datasourceimpl

import android.util.Log
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
    private val firestore: FirebaseStorage
): FirebaseDataSource {

    private val usersRef = firebaseRtdb.getReference("users")
    private val profileRef = firestore.getReference("profile")
    private val advertiseRef = firestore.getReference("advertise")
    private val imagesRef = firebaseRtdb.getReference("images")
    private val chatRoomRef = firebaseRtdb.getReference("chatrooms")
    private val chatRoomIdRef = firebaseRtdb.getReference("usersChatRooms")
    private val messageRef = firebaseRtdb.getReference("messages")

    //채팅방 생성
    override suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom): Response<DomainChatRoom> {
        return try {
            Log.d("createChatRoom_DataSourceImpl", "채팅방 생성 요청: $chatRoom")
            val roomId = chatRoomRef.push().key ?: return Response.Error("채팅방 ID 생성 실패")

            chatRoomRef.child(roomId).setValue(chatRoom.copy(roomId = roomId)).await()
            Log.d("createChatRoom_DataSourceImpl", "채팅방 저장 완료: $roomId")
            val updates = mapOf(
                "/$uid/$roomId" to true,
                "/$destUid/$roomId" to true
            )
            chatRoomIdRef.updateChildren(updates).await()
            Log.d("createChatRoom_DataSourceImpl", "사용자별 채팅방 ID 저장 완료")

            val newMessageId = messageRef.child(roomId).push()
            newMessageId.setValue(chatRoom.lastMessage).await()
            Log.d("createChatRoom_DataSourceImpl", "사용자 새 채팅방 첫 메세지 저장 완료")

            Response.Success(chatRoom.copy(roomId = roomId))
        }catch (e: Exception){
            Log.e("createChatRoom_DataSourceImpl", "채팅방 생성 실패: ${e.message}", e)
            Response.Error("실패", e)
        }
    }

    override suspend fun getAdvertiseImages(): Response<List<String>> {
        return try {
            val fileRef = advertiseRef.listAll().await()
            val urls = fileRef.items.map { it.downloadUrl.await().toString() }
            Response.Success(urls)
        }catch (e: Exception){
            Response.Error("이미지 목록을 불러오는데 실패하였습니다.", exception = e)
        }
    }

    //메세지 전송
    override suspend fun sendMessage(chatroom: DomainChatRoom, message: DomainMessage): Response<Boolean> {
        return try {
            val newMessageRef = messageRef.child(chatroom.roomId).push()
            newMessageRef.setValue(message).await()

            chatRoomRef.child(chatroom.roomId).child("unreadMessages").setValue(chatroom.unreadMessages)
            Log.d("sendMeesage_DatasourceImpl", chatroom.unreadMessages.toString())

            chatRoomRef.child(chatroom.roomId).child("lastMessage").updateChildren(
                mapOf(
                    "message" to message.message,
                    "timestamp" to message.timestamp,
                    "senderUid" to message.senderUid
                )
            ).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error("메세지 전송 실패", e)
        }
    }

    override fun markMessageAsRead(chatroomId: String, userUid: String) {
        chatRoomRef.child(chatroomId).child("unreadMessages/${userUid}").setValue(0)
    }

    override suspend fun observeChatRoom(chatRoomId: String) = callbackFlow {
        val chatRoomRef = chatRoomRef.child(chatRoomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(DomainChatRoom::class.java)?.let { chatRoom ->
                    trySend(chatRoom)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        chatRoomRef.addValueEventListener(listener)

        awaitClose { chatRoomRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    //메시지 가져오기
    override suspend fun loadMessage(chatroomId: String, uid: String) = callbackFlow  {
        var ref = messageRef.child(chatroomId)

        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<DomainMessage>()
                snapshot.children.forEach { dataSnapshot ->
                    val message = dataSnapshot.getValue(DomainMessage::class.java)

                    if(message != null){
                        messageList.add(message)
                    }
                }
                markMessageAsRead(chatroomId, uid)
                trySend(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(valueListener)
        awaitClose{
            ref.removeEventListener(valueListener)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserChatRoomLists(uid: String) = callbackFlow {
        val ref = firebaseRtdb.getReference("chatrooms")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var chatList = mutableListOf<DomainChatRoomWithUser>()
                val deferredList = mutableListOf<Deferred<Boolean>>()
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                for(chatRoomModel in snapshot.children){
                    val chatRoom = chatRoomModel.getValue(DomainChatRoom::class.java)!!
                    Log.d("getUserChatRoomLists_DataSourceImpl", "${uid}님의 채팅방 발견: ${chatRoom.toString()}")

                    val deferred = coroutineScope.async {
                        val artworkSnapshot = imagesRef.child(chatRoom.artworkId).get().await()
                        val userSnapshot = usersRef.child(chatRoom.users.keys.filter { it != uid }.first()).get().await()
                        val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)!!
                        val destUser = userSnapshot.getValue(DomainUser::class.java)!!

                        Log.d("getUserChatRoomLists_DataSourceImpl", "artwork: ${artwork}, destUser: ${destUser}")
                        chatList.add(
                            DomainChatRoomWithUser(destUser = destUser, artwork = artwork, chatRoom = chatRoom)
                        )
                    }
                    deferredList.add(deferred)
                }
                coroutineScope.launch {
                    deferredList.awaitAll()
                    trySend(chatList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.orderByChild("users/"+uid).equalTo(true).addValueEventListener(listener)
        awaitClose{
            ref.removeEventListener(listener)
        }
    }

    // 채팅방 유무 확인
    override suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<DomainChatRoom?> {
        return try {
            val chatRoomSnapshot = chatRoomRef.orderByChild("artworkId").equalTo(artworkId).get().await()
            for(chat in chatRoomSnapshot.children){
                val chatRoom = chat.getValue(DomainChatRoom::class.java)
                if(chatRoom != null){
                    Log.d("checkChatRoom", "Found chatRoom: ${chat.key}, users: ${chatRoom.users.keys}")
                    val userSet = chatRoom.users.keys
                    if(uid in userSet && destUid in destUid){
                        return Response.Success(chatRoom)
                    }
                }
            }
            Response.Success(null)
        }catch (e: Exception){
            Response.Error("DB 에러", e)
        }
    }

    override suspend fun getUserInfoLists(
        uid: List<String>,
    ): Response<List<DomainUser>> {
        return try {
            val userList = uid.mapNotNull { userId ->
                val snapshot = usersRef
                    .child(userId)
                    .get()
                    .await()

                snapshot.getValue(DomainUser::class.java)
            }
            Response.Success(userList)
        } catch (e: Exception) {
            Response.Error(e.toString())
        }
    }
}