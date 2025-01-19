package com.example.domain.repository

import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    fun saveUserInfo(user: DomainUser): Task<Void>

    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean, category: String): Task<Void>

    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void>

    fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener)

    suspend fun deleteUserAccount(uid: String): Boolean

    suspend fun getArtworkLists(category: String): List<DomainArtwork>

    suspend fun getFavoriteArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>

}