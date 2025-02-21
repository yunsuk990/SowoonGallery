package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.DomainArtwork
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface ArtworkRepository {


    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean): Task<Void>

    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean): Task<Void>

    fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun getLikedCountArtwork(artworkUid: String, listener: ValueEventListener)

    suspend fun uploadNewArtwork(artwork: DomainArtwork, imageUri: Uri): Response<Boolean>

    suspend fun uploadNewArtwork(artworkList: List<Pair<Uri, DomainArtwork>>): Response<Boolean>

    suspend fun getArtworkLists(): List<DomainArtwork>

    suspend fun getFavoriteArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getRecentArtworks(limit: Int): List<DomainArtwork>

    suspend fun getArtistArtworks(artistId: String): List<DomainArtwork>

    suspend fun getArtworkById(artworkId: String): DomainArtwork

    suspend fun getArtistSoldArtworks(artworksUid: Map<String, Boolean>): List<DomainArtwork>

}