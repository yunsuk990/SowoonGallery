package com.example.data.repository.remote.datasourceimpl

import com.example.data.model.DataUser
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.DomainArtwork
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
        private val firestore: FirebaseStorage
): FirebaseDataSource {

    private val usersRef = firebaseRtdb.getReference("users")
    private val imagesRef = firebaseRtdb.getReference("images")

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

    // 사용자 정보를 db에 저장
    override fun saveUserInfo(uid: String, user: DataUser): Task<Void> = usersRef.child(uid).setValue(user)

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