package com.example.data.repository.remote.datasourceimpl

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.data.model.DataUser
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainPrice
import com.example.domain.model.DomainUser
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
    private val firestore: FirebaseStorage
): FirebaseDataSource {

    private val usersRef = firebaseRtdb.getReference("users")
    private val imagesRef = firebaseRtdb.getReference("images")
    private val storageRef = firestore.getReference("profile")

    //작품 전체 가져오기
    override suspend fun getAllArtworks(): List<DomainArtwork> {
        val itemList = mutableListOf<DomainArtwork>()
        val snapshot = imagesRef.get().await()
        snapshot?.children?.forEach{categorySnapshot ->
            categorySnapshot.children.forEach { artworkSnapshot ->
                val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
                artwork!!.key = artworkSnapshot.key
                itemList.add(artwork)
            }
        }
        return itemList
    }

    //카테고리별 작품 가져오기
    override suspend fun getArtworksByCategory(category: String): List<DomainArtwork> {
        val itemList = mutableListOf<DomainArtwork>()
        val snapshot = imagesRef.child(category).get().await()
        snapshot?.children?.forEach{ artworkSnapshot ->
            val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
            artwork!!.key = artworkSnapshot.key
            itemList.add(artwork)
        }
        return itemList
    }

    override suspend fun uploadImageToStorage(uid: String, uri: Uri): Response<String>{
        val fileRef = storageRef.child("${uid}.jpg")
        return try {
            fileRef.putFile(uri).await() // 이미지 업로드
            val downloadUrl = fileRef.downloadUrl.await().toString() // 다운로드 URL 반환
            Response.Success(downloadUrl)
        }catch (e: Exception){
            Response.Error("Image upload failed", e)
        }
    }

    override suspend fun updateUserProfile(
        uid: String,
        updateFields: Map<String, Any>
    ): Response<Boolean> {
        return try {
            usersRef.child(uid).updateChildren(updateFields).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error("Failed to update user profile", e)
        }
    }

    override fun updateUserProfile(uid: String, profile: DomainUser) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun savePriceForArtwork(
        category: String,
        artworkId: String,
        price: Float,
        userId: String,
    ): Task<Void> {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd")
        val formattedDate = currentDate.format(formatter)
        val domainPriceData = DomainPrice(price, userId)
        val priceRef = imagesRef.child(category).child(artworkId).child("prices")
        return priceRef.get().continueWithTask { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val updates = hashMapOf<String, Any?>()

                // Step 2: 사용자의 이전 데이터 삭제
                snapshot?.children?.forEach { dateSnapshot ->
                    val domainPrice = dateSnapshot.getValue(DomainPrice::class.java)
                    if (domainPrice != null && domainPrice.userId == userId && dateSnapshot.key != formattedDate) {
                        updates[dateSnapshot.key!!] = null
                    }
                }

                // Step 3: 당일 데이터 추가
                updates[formattedDate] = domainPriceData
                Log.d("FirebaseDataSource_savePriceForArtwork", "save_update")
                // Step 4: Firebase 업데이트 실행
                priceRef.updateChildren(updates)
            } else {
                throw task.exception ?: Exception("Failed to fetch prices")
            }
        }
        //return imagesRef.child(category).child(artworkId).child("prices").child(formattedDate).(domainPriceData)
    }

    override fun getPriceForArtwork(
        category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit
    ){
        imagesRef.child(category).child(artworkId).child("prices").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val priceList = mutableListOf<PriceWithUser>()
                var uidLists = mutableListOf<String>()
                snapshot?.children?.forEach{snapshot ->
                    var domainPrice = snapshot.getValue(DomainPrice::class.java)!!
                    uidLists.add(domainPrice.userId)
                    priceList.add(PriceWithUser(domainPrice.userId, "", 0, domainPrice.price, snapshot.key!!))
                }

                // 모든 uid에 대해 사용자 정보를 가져온 후 업데이트
                val pendingTasks = mutableListOf<Task<DataSnapshot>>()
                uidLists.forEach { uid ->
                    pendingTasks.add(usersRef.child(uid).get())
                }

                Tasks.whenAllComplete(pendingTasks).addOnCompleteListener {
                    pendingTasks.forEachIndexed { index, task ->
                        if (task.isSuccessful) {
                            val userSnapshot = task.result
                            val userInfo = userSnapshot?.getValue(DomainUser::class.java)
                            val userId = uidLists.elementAt(index)

                            // priceList에 사용자 정보 업데이트
                            priceList.filter { it.uid == userId }.forEach {
                                it.name = userInfo?.name ?: "Unknown"
                                it.age = userInfo?.age ?: 0
                            }
                        }
                    }
                    // 모든 데이터를 업데이트한 후 콜백 호출
                    Log.d("FirebaseDataSource_getPriceForArtwork", priceList.toString())
                    callback(priceList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    // 사용자 정보를 db에 저장
    override fun saveUserInfo(uid: String, user: DataUser): Task<Void> = usersRef.child(uid).setValue(user)

    override fun getUserInfo(uid: String, callback: (Response<DomainUser>) -> Unit) {
        usersRef.child(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userInfo = snapshot.getValue(DomainUser::class.java)
                if( userInfo != null){
                    userInfo?.uid = uid
                    callback(Response.Success(userInfo))
                }else{
                    callback(Response.Error("사용자 정보가 없습니다"))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(Response.Error(error.message))
            }
        })
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

    // 등록된 사용자인지 확인
    override fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot> = usersRef.child(uid).get()

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult> = firebaseAuth.signInWithCredential(credential)

    // 작품 북마크 설정
    override fun setFavoriteArtwork(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
        category: String
    ): Task<Void> {
        return if(!isFavorite){
            usersRef.child(uid).child("favoriteArtworks").child(artworkUid).removeValue()
            imagesRef.child(category).child(artworkUid).child("favoriteUser").child(uid).removeValue()
        }else{
            usersRef.child(uid).child("favoriteArtworks").child(artworkUid).setValue(category)
            imagesRef.child(category).child(artworkUid).child("favoriteUser").child(uid).setValue(isFavorite)
        }
    }

    // 사용자가 저장한 작품들 가져오기
    override fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = usersRef.child(uid).child("favoriteArtworks").child(artworkUid).get()

    // 사용자 작품 좋아요 설정
    override fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void> {
        return if(!isLiked){
            usersRef.child(uid).child("likedArtworks").child(artworkUid).removeValue()
            imagesRef.child(category).child(artworkUid).child("likedArtworks").child(uid).removeValue()
        }else{
            usersRef.child(uid).child("likedArtworks").child(artworkUid).setValue(category)
            imagesRef.child(category).child(artworkUid).child("likedArtworks").child(uid).setValue(isLiked)
        }
    }

    // 사용자가 좋아요한 작품들 가져오기
    override fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = usersRef.child(uid).child("likedArtworks").child(artworkUid).get()

    override fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener) {
        imagesRef.child(category).child(artworkUid).child("likedArtworks").addValueEventListener(listener)
    }

    // Firebase Auth에서 사용자 삭제
    override suspend fun deleteAccount(uid: String): Boolean {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            true
        }catch (e: Exception) {
            false
        }
    }

    // 사용자 데이터 삭제
    override suspend fun deleteUserRtdb(uid: String): Boolean {
        return try {
            val result = usersRef.child(uid).removeValue().await()
            result != null
        }catch (e: Exception){
            false
        }
    }

    //사용자가 좋아요한 작품에서 삭제
    override suspend fun removeUserFromLikedArtworks(uid: String): Boolean {
        return try {
            val likedArtworkSnapshot = usersRef.child(uid).child("likedArtworks").get().await()
            val likedArtworkIds = likedArtworkSnapshot.children.mapNotNull { it.key }
            likedArtworkIds.forEach { artworkId ->
                imagesRef.child(artworkId).child("likedUsers").child(uid).removeValue().await()
            }
            firebaseAuth.signOut()
            true
        }catch (e: Exception){
            false
        }
    }

    override suspend fun getFavoritesArtwork(uid: String): Flow<List<DomainArtwork>> = flow {
        val favoriteArtworkSnapshot = usersRef.child(uid).child("favoriteArtworks").get().await()
        val artworkData = mutableListOf<DomainArtwork>()
        if(favoriteArtworkSnapshot.exists()){
            for(artworkSnapshot in favoriteArtworkSnapshot.children){
                val id = artworkSnapshot.key
                val category = artworkSnapshot.value as String
                val artwork = imagesRef.child(category).child(id!!).get().await()
                val domainArtwork = artwork.getValue(DomainArtwork::class.java)!!
                domainArtwork.key = id
                artworkData.add(domainArtwork)
            }
        }
        emit(artworkData)
    }.flowOn(Dispatchers.IO)

    override suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>> = flow {
        val likedArtworkSnapshot = usersRef.child(uid).child("likedArtworks").get().await()
        val artworkData = mutableListOf<DomainArtwork>()
        for(artworkSnapshot in likedArtworkSnapshot.children){
            val id = artworkSnapshot.key
            val category = artworkSnapshot.value as String
            val artwork = imagesRef.child(category).child(id!!).get().await()
            val domainArtwork = artwork.getValue(DomainArtwork::class.java)!!
            domainArtwork.key = id
            artworkData.add(domainArtwork)
        }
        emit(artworkData)
    }


}