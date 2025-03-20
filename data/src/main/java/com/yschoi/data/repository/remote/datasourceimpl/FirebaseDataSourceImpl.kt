package com.yschoi.data.repository.remote.datasourceimpl

import android.content.Context
import com.yschoi.data.repository.remote.datasource.FirebaseDataSource
import com.yschoi.domain.model.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
    private val firestore: FirebaseStorage,
    @ApplicationContext private val context: Context
): FirebaseDataSource {

    private var cachedToken: String? = null
    private val usersRef = firebaseRtdb.getReference("users")
    private val profileRef = firestore.getReference("profile")
    private val advertiseRef = firestore.getReference("advertise")
    private val advertiseRTDBRef = firebaseRtdb.getReference("advertiseImages")
    private val imagesRef = firebaseRtdb.getReference("images")
    private val chatRoomRef = firebaseRtdb.getReference("chatrooms")
    private val chatRoomIdRef = firebaseRtdb.getReference("usersChatRooms")
    private val messageRef = firebaseRtdb.getReference("messages")
    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/sowoon-849bd/messages:send"

    //채팅방 생성
    override suspend fun createChatRoom(uid: String, destUid: String, chatRoom: DomainChatRoom): Response<DomainChatRoom> {
        return try {
            val roomId = getChatRoomId(userId = uid, artistId = destUid, artworkId = chatRoom.artworkId)
            val roomRef = chatRoomRef.child(roomId)
            roomRef.setValue(chatRoom.copy(roomId = roomId)).await()
            val updates = mapOf(
                "/$uid/chatrooms/$roomId" to true,
                "/$destUid/chatrooms/$roomId" to true
            )
            chatRoomIdRef.updateChildren(updates).await()

            val newMessageId = messageRef.child(roomId).push()
            newMessageId.setValue(chatRoom.lastMessage).await()
            Response.Success(chatRoom.copy(roomId = roomId))
        }catch (e: Exception){
            Response.Error("실패", e)
        }
    }

    override suspend fun getAdvertiseImages(): Response<List<String>> {
        return try {
            val imageSnapshot = advertiseRTDBRef.child("images").get().await()
            val urls = imageSnapshot.children.map { it.value.toString() }
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

    override fun getChatRoomId(userId: String, artistId: String, artworkId: String): String {
        return "${userId}_${artistId}_${artworkId}"
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

    override suspend fun sendFCMMessage(notificationModel: NotificationModel) {
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", notificationModel.token)
                        put("notification", JSONObject().apply {
                            put("body", notificationModel.notification.body)
                            put("title", notificationModel.notification.title)
                        })
                        put("data", JSONObject().apply {
                            put("body", notificationModel.notification.body)
                            put("title", notificationModel.notification.title)
                            put("artwork", Gson().toJson(notificationModel.artwork))
                            put("destUser", Gson().toJson(notificationModel.destUser))
                            put("chatRoomWithUser", Gson().toJson(notificationModel.chatRoom))
                        })
                    })
                }
                val accessToken = getFirebaseToken()

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(fcmUrl)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun leaveChatRoom(roomId: String, uid: String) {
        try {
            chatRoomRef.child(roomId).child("status/${uid}").setValue(false)
        }catch (e:Exception){
        }
    }

    override suspend fun enterChatRoom(roomId: String, uid: String) {
        try {
            chatRoomRef.child(roomId).child("status/${uid}").setValue(true)
        }catch (e:Exception){

        }
    }

    override suspend fun deleteChatRoom(uid: String) {
        val chatRoomSnapshot = chatRoomIdRef.child(uid).child("chatrooms").get().await()
        for(chatRoom in chatRoomSnapshot.children){
            val roomId = chatRoom.key!!
            chatRoomRef.child(roomId).child("users").child(uid).removeValue().await()
            var userSnapshot = chatRoomRef.child(roomId).child("users").get().await()
            if(!userSnapshot.exists()){
                chatRoomRef.child(roomId).removeValue()
                messageRef.child(roomId).removeValue()
            }
            chatRoomIdRef.child(uid).child("chatrooms").child(roomId).removeValue()
        }
    }

    override suspend fun exitChatRoom(roomId: String, uid: String): Response<Boolean> {
        return try {
            chatRoomRef.child(roomId).removeValue().await()
            messageRef.child(roomId).removeValue().await()
            chatRoomIdRef.child(uid).child("chatrooms").child(roomId).removeValue().await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error(e.message.toString(),e)
        }
    }

    override suspend fun observeStatus(roomId: String): Flow<Map<String, Boolean>> = callbackFlow{
        val ref =  chatRoomRef.child(roomId).child("status")
        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(object : GenericTypeIndicator<Map<String, Boolean>>() {})
                if (status != null) {
                    trySend(status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

       ref.addValueEventListener(valueEventListener)

        awaitClose{
            ref.removeEventListener(valueEventListener)
        }
    }

    override suspend fun observeUnReadMessages(roomId: String): Flow<Map<String, Int>> = callbackFlow{
        val ref = chatRoomRef.child(roomId).child("unreadMessages")
        val valueEventListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(object : GenericTypeIndicator<Map<String, Int>>() {})
                if (status != null) {
                    trySend(status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(valueEventListener)
        awaitClose{
            ref.removeEventListener(valueEventListener)
        }
    }

    suspend fun getFirebaseToken(): String {
        return withContext(Dispatchers.IO) {
            try {
                if (cachedToken == null) {
                    val SCOPES = "https://www.googleapis.com/auth/firebase.messaging"
                    val credentials = GoogleCredentials
                        .fromStream(context.assets.open("fcm_service_account.json"))
                        .createScoped(listOf(SCOPES))
                    credentials.refreshIfExpired()
                    cachedToken = credentials.accessToken.tokenValue
                }
                cachedToken!!
            } catch (e: Exception) {
                throw e
            }
        }
    }



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
                if(!messageList.isEmpty()){
                    markMessageAsRead(chatroomId, uid)
                }
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
        val userChatRoomsRef = chatRoomIdRef.child(uid).child("chatrooms")
        val chatRoomListeners = mutableMapOf<String, ValueEventListener>()

        val userChatRoomsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    trySend(emptyList())
                    return
                }
                val chatRooms = mutableListOf<DomainChatRoomWithUser>()
                val deferredList = mutableListOf<Deferred<Boolean>>()
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                //채팅방 id 리스트
                val currentRoomIds = snapshot.children.mapNotNull { it.key }
                // 삭제된 채팅방 리스너 정리
                val removedRooms = chatRoomListeners.keys - currentRoomIds.toSet()
                for (roomId in removedRooms) {
                    chatRoomListeners[roomId]?.let {
                        firebaseRtdb.getReference("chatrooms").child(roomId).removeEventListener(it)
                        chatRoomListeners.remove(roomId)
                    }
                    // 채팅방이 삭제되었으므로 목록에서 제거
                    synchronized(chatRooms) {
                        chatRooms.removeAll { it.chatRoom.roomId == roomId }
                    }
                }

                for (roomId in currentRoomIds) {
                    val chatRoomRef = firebaseRtdb.getReference("chatrooms").child(roomId)
                    // 기존 리스너가 있으면 제거
                    chatRoomListeners[roomId]?.let { chatRoomRef.removeEventListener(it) }
                    // 새로운 리스너 생성 및 등록
                    val chatRoomListener = object : ValueEventListener {
                        override fun onDataChange(roomSnapshot: DataSnapshot) {
                            val chatRoom = roomSnapshot.getValue(DomainChatRoom::class.java) ?: return
                            // 사용자가 방을 나갔는지 체크
                            if (!chatRoom.users.contains(uid)) {
                                // 방 나갔을 경우 채팅방 목록에서 제거
                                synchronized(chatRooms) {
                                    chatRooms.removeAll { it.chatRoom.roomId == chatRoom.roomId }
                                }
                                trySend(chatRooms.toList()) // UI에 갱신된 목록 전송
                                return
                            }


                            val deferred = coroutineScope.async {
                                val artworkSnapshot = imagesRef.child(chatRoom.artworkId).get().await()
                                val destUid = chatRoom.users.keys.firstOrNull { it != uid }
                                val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)!!
                                val destUser = destUid?.let {
                                    val userSnapshot = usersRef.child(it).get().await()
                                    userSnapshot.getValue(DomainUser::class.java)
                                }

                                synchronized(chatRooms) {
                                    chatRooms.removeAll { it.chatRoom.roomId == chatRoom.roomId }
                                    chatRooms.add(DomainChatRoomWithUser(destUser, chatRoom, artwork))
                                }
                            }
                            deferredList.add(deferred)
                            coroutineScope.launch {
                                deferredList.awaitAll()
                                trySend(chatRooms.toList())
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    }

                    chatRoomRef.addValueEventListener(chatRoomListener)
                    chatRoomListeners[roomId] = chatRoomListener
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }

        userChatRoomsRef.addValueEventListener(userChatRoomsListener)

        awaitClose {
            userChatRoomsRef.removeEventListener(userChatRoomsListener)
            chatRoomListeners.forEach { (roomId, listener) ->
                firebaseRtdb.getReference("chatrooms").child(roomId).removeEventListener(listener)
            }
            chatRoomListeners.clear()
        }
    }

    // 채팅방 유무 확인
    override suspend fun checkChatRoom(uid: String, destUid: String, artworkId: String): Response<DomainChatRoom?> {
        return try {
            val chatRoomId = getChatRoomId(userId = uid, artistId = destUid, artworkId = artworkId )
            val chatRoomSnapshot = chatRoomRef.child(chatRoomId).get().await()
            if(chatRoomSnapshot.exists()){
                val chatRoom = chatRoomSnapshot.getValue(DomainChatRoom::class.java)
                Response.Success(chatRoom)
            }else{
                Response.Success(null)
            }
        }catch (e: Exception){
            Response.Error("checkChatRoom_DataSourceImpl", e)
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