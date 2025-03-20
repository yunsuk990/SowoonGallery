package com.yschoi.data.repository.remote.datasourceimpl

import android.net.Uri
import com.yschoi.data.repository.remote.datasource.ArtworkDataSource
import com.yschoi.domain.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ArtworkDataSourceImpl @Inject constructor(
    private val firebaseRtdb: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
): ArtworkDataSource {

    private val imagesRef = firebaseRtdb.getReference("images")
    private val usersRef = firebaseRtdb.getReference("users")
    private val artworkIdRef = firebaseRtdb.getReference("artworkIds")
    private val imageStorageRef = firebaseStorage.getReference("images")
    private val profileStorageRef = firebaseStorage.getReference("profiles")

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

    override fun getLikedCountArtwork(artworkUid: String, listener: ValueEventListener) {
        imagesRef.child(artworkUid).child("likedArtworks").addValueEventListener(listener)
    }

    override suspend fun getRecentArtworks(limit: Int): List<DomainArtwork> {
        return try {
            val artworksSnapshot = imagesRef.orderByChild("upload_at").limitToLast(limit).get().await()
            var artworkList = mutableListOf<DomainArtwork>()
            for(artwork in artworksSnapshot.children){
                val artworkData = artwork.getValue(DomainArtwork::class.java)
                artworkData?.let { artworkList.add(it) }
            }
            artworkList
        }catch (e: Exception){
            emptyList()
        }
    }

    //작품 이미지 스토리지에 저장 및 URL 반환 (이미지: 0, 프로필: 1)
    override suspend fun uploadImageToStorage(name: String, imageUri: Uri, mode: Int): String {
        if(mode == 0){
            val uploadTask = imageStorageRef.child("${name}_${System.currentTimeMillis()}.jpg").putFile(imageUri).await()
            return uploadTask.storage.downloadUrl.await().toString()
        }else{
            val uploadTask = profileStorageRef.child("${name}_${System.currentTimeMillis()}.jpg").putFile(imageUri).await()
            return uploadTask.storage.downloadUrl.await().toString()
        }
    }

    //작품 DB에 저장
    override suspend fun uploadImageToRTDB(artwork: DomainArtwork): Response<Boolean> {
        return try {
            val artworkId = imagesRef.push().key
            artwork.key = artworkId!!
            imagesRef.child(artworkId).setValue(artwork).await()
            usersRef.child(artwork.artistUid!!).child("artworksUid").updateChildren(mapOf(artworkId to false)).await()
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

    override suspend fun removeUserFromBookmarkArtworks(uid: String): Boolean {
        return try {
            val bookmarkArtworkSnapshot = usersRef.child(uid).child("favoriteArtworks").get().await()
            val bookmarkArtworkIds = bookmarkArtworkSnapshot.children.mapNotNull { it.key }
            bookmarkArtworkIds.forEach { artworkId ->
                imagesRef.child(artworkId).child("favoriteUser").child(uid).removeValue().await()
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

    override fun updateArtworkSoldState(
        artistUid: String,
        artworkId: String,
        sold: Boolean,
        destUid: String?
    ) {
        usersRef.child(artistUid).child("artworksUid").child(artworkId).setValue(sold)
        imagesRef.child(artworkId).child("sold").setValue(sold)

        if(destUid != null){
            val updateMap = mapOf("purchasedArtworks/$artworkId" to true)
            usersRef.child(destUid).updateChildren(updateMap)
        }
    }

    override suspend fun getArtistSoldArtworks(artworksUid: Map<String, Boolean>): List<DomainArtwork> = coroutineScope {
        if (artworksUid.isEmpty()) return@coroutineScope emptyList()

        // sold가 true인 UID만 필터링
        val soldArtworksUid = artworksUid.filterValues { it }.keys

        // Firebase에서 여러 UID를 한 번에 조회
        val deferredArtworks = async {
            val result = soldArtworksUid.mapNotNull { uid ->
                val snapshot = imagesRef.child(uid).get().await()
                snapshot.getValue(DomainArtwork::class.java)
            }
            result
        }

        // 결과 반환
        deferredArtworks.await()
    }
}