package com.example.data.repository.remote.datasourceimpl

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.domain.model.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

class ArtworkDataSourceImpl @Inject constructor(
    private val firebaseRtdb: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
): ArtworkDataSource {

    private val imagesRef = firebaseRtdb.getReference("images")
    private val usersRef = firebaseRtdb.getReference("users")
    private val artworkIdRef = firebaseRtdb.getReference("artworkIds")
    private val imageStorageRef = firebaseStorage.getReference("images")

    //작품 전체 가져오기
    override suspend fun getAllArtworks(): List<DomainArtwork> {
        val itemList = mutableListOf<DomainArtwork>()
        val snapshot = imagesRef.get().await()
        snapshot?.children?.forEach{ artworkSnapshot ->
            val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
            itemList.add(artwork!!)
        }
        return itemList
    }

    //카테고리별 작품 가져오기
    override suspend fun getArtworksByCategory(category: String): List<DomainArtwork> {
        val itemList = mutableListOf<DomainArtwork>()
        //카테고리의 작품 ID 가져오기
        val snapshot = artworkIdRef.child(category).get().await()
        snapshot.children.forEach{ artworkIdSnapshot ->
            val artworkSnapshot = imagesRef.child(artworkIdSnapshot.key!!).get().await()
            val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
            itemList.add(artwork!!)
        }
        return itemList
    }

    override suspend fun getArtworkById(artworkUid: String): DomainArtwork {
        val artworkSnapshot = imagesRef.child(artworkUid).get().await()
        val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)!!
        return artwork
    }

    // 작품 북마크 설정
    override fun setFavoriteArtwork(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
    ): Task<Void> {
        return if(!isFavorite){
            usersRef.child(uid).child("favoriteArtworks").child(artworkUid).removeValue()
            imagesRef.child(artworkUid).child("favoriteUser").child(uid).removeValue()
        }else{
            usersRef.child(uid).child("favoriteArtworks").child(artworkUid).setValue(true)
            imagesRef.child(artworkUid).child("favoriteUser").child(uid).setValue(isFavorite)
        }
    }

    // 사용자가 저장한 작품들 가져오기
    override fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = usersRef.child(uid).child("favoriteArtworks").child(artworkUid).get()

    // 사용자 작품 좋아요 설정
    override fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean): Task<Void> {
        return if(!isLiked){
            usersRef.child(uid).child("likedArtworks").child(artworkUid).removeValue()
            imagesRef.child(artworkUid).child("likedArtworks").child(uid).removeValue()
        }else{
            usersRef.child(uid).child("likedArtworks").child(artworkUid).setValue(true)
            imagesRef.child(artworkUid).child("likedArtworks").child(uid).setValue(isLiked)
        }
    }

    // 사용자가 좋아요한 작품들 가져오기
    override fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = usersRef.child(uid).child("likedArtworks").child(artworkUid).get()

    override fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener) {
        imagesRef.child(category).child(artworkUid).child("likedArtworks").addValueEventListener(listener)
    }

    override suspend fun getRecentArtworks(limit: Int): List<DomainArtwork> {
        return try {
            val artworksSnapshot = imagesRef.orderByChild("upload_at").limitToLast(limit).get().await()
            var artworkList = mutableListOf<DomainArtwork>()
            Log.d("getRecentArtworks", artworksSnapshot.toString())
            for(artwork in artworksSnapshot.children){
                val artworkData = artwork.getValue(DomainArtwork::class.java)
                artworkData?.let { artworkList.add(it) }
            }
            artworkList
        }catch (e: Exception){
            Log.d("getRecentArtworks_error", e.toString())
            emptyList()
        }
    }

    //작품 이미지 스토리지에 저장 및 URL 반환
    override suspend fun uploadImageToStorage(imageUri: Uri): String {
        val uploadTask = imageStorageRef.child("${System.currentTimeMillis()}.jpg").putFile(imageUri).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    //작품 DB에 저장
    override suspend fun uploadImageToRTDB(artwork: DomainArtwork): Response<Boolean> {
        return try {
            Log.d("uploadArtwork", artwork.toString())
            val artworkId = imagesRef.push().key
            artwork.key = artworkId!!
            imagesRef.child(artworkId).setValue(artwork).await()
            usersRef.child(artwork.artistUid!!).child("artworksUid").updateChildren(mapOf(artworkId to true)).await()
            artworkIdRef.child(artwork.category!!).child(artwork.key!!).setValue(true).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error("업로드 실패", e)
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
            true
        }catch (e: Exception){
            false
        }
    }

    // 사용자가 저장한 작품들 가져오기
    override suspend fun getFavoritesArtwork(uid: String): Flow<List<DomainArtwork>> = flow {
        val favoriteArtworkSnapshot = usersRef.child(uid).child("favoriteArtworks").get().await()
        val artworkData = mutableListOf<DomainArtwork>()
        for(artworkId in favoriteArtworkSnapshot.children){
            val id = artworkId.key
            val artwork = imagesRef.child(id!!).get().await()
            val domainArtwork = artwork.getValue(DomainArtwork::class.java)!!
            artworkData.add(domainArtwork)
        }
        emit(artworkData)
    }.flowOn(Dispatchers.IO)

    override suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>> = flow {
        val likedArtworkSnapshot = usersRef.child(uid).child("likedArtworks").get().await()
        val artworkData = mutableListOf<DomainArtwork>()
        for(artworkId in likedArtworkSnapshot.children){
            val id = artworkId.key
            val artwork = imagesRef.child(id!!).get().await()
            val domainArtwork = artwork.getValue(DomainArtwork::class.java)!!
            artworkData.add(domainArtwork)
        }
        emit(artworkData)
    }.flowOn(Dispatchers.IO)

    override suspend fun getArtistArtworks(artistUid: String): List<DomainArtwork> {
        val artworks = usersRef.child(artistUid).child("artworksUid").get().await()
        val artworkData = mutableListOf<DomainArtwork>()
        for( snapshot in artworks.children ){
            var artworkId = snapshot.key
            var artwork = imagesRef.child(artworkId!!).get().await()
            artworkData.add(artwork.getValue(DomainArtwork::class.java)!!)
        }
        return artworkData
    }

    override fun updateArtworkSoldState(artworkId: String, sold: Boolean) {
        imagesRef.child(artworkId).child("sold").setValue(sold)
    }

}