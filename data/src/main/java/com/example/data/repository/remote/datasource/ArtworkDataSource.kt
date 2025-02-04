package com.example.data.repository.remote.datasource

import android.net.Uri
import com.example.domain.model.DomainArtwork
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface ArtworkDataSource {

    //작품 전체 가져오기
    suspend fun getAllArtworks(): List<DomainArtwork>

    //카테고리별 작품 가져오기
    suspend fun getArtworksByCategory(category: String): List<DomainArtwork>

    //작품 가격 제시
    fun savePriceForArtwork(category: String, artworkId: String, price: Float, userId: String): Task<Void>

    fun getPriceForArtwork(category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit)

    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean): Task<Void>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean): Task<Void>

    fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener)

    suspend fun getRecentArtworks(limit: Int ): List<DomainArtwork>

    suspend fun uploadImageToStorage(imageUri: Uri): String

    suspend fun uploadImageToRTDB(artwork: DomainArtwork): Response<Boolean>

    suspend fun removeUserFromLikedArtworks(uid: String): Boolean

    suspend fun getFavoritesArtwork(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getArtistArtworks(artistUid: String): List<DomainArtwork>

}