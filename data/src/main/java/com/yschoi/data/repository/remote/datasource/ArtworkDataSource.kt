package com.yschoi.data.repository.remote.datasource

import android.net.Uri
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface ArtworkDataSource {

    //작품 전체 가져오기
    suspend fun getAllArtworks(): List<DomainArtwork>

    //카테고리별 작품 가져오기
    suspend fun getArtworksByCategory(category: String): List<DomainArtwork>

    suspend fun getArtworkById(artworkUid: String): DomainArtwork

    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean): Task<Void>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean): Task<Void>

    suspend fun getRecentArtworks(limit: Int ): List<DomainArtwork>

    suspend fun uploadImageToStorage(name: String, imageUri: Uri, mode: Int): String

    suspend fun uploadImageToRTDB(artwork: DomainArtwork): Response<Boolean>

    suspend fun removeUserFromLikedArtworks(uid: String): Boolean

    suspend fun removeUserFromBookmarkArtworks(uid: String): Boolean

    suspend fun getFavoritesArtwork(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getArtistArtworks(artistUid: String): Flow<List<DomainArtwork>>

    fun updateArtworkSoldState(artistUid: String, artworkId: String, sold: Boolean, destUid: String?)

    suspend fun getArtistSoldArtworks(artworksUid: Map<String, Boolean>): List<DomainArtwork>

    suspend fun fetchArtwork(artworkId: String): Flow<DomainArtwork>
}