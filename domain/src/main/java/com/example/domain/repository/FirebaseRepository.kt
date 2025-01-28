package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {

    fun saveUserInfo(user: DomainUser): Task<Void>

    fun getUserInfo(uid: String, callback: (Response<DomainUser>) -> Unit)

    suspend fun uploadProfileImage(
        uid: String,
        uri: Uri?,
        currentUser: DomainUser,
        name: String,
        age: Int
    ): Response<Boolean>

    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    suspend fun getUserInfoLists(uid: List<String>): Response<List<DomainUser>>

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean, category: String): Task<Void>

    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void>

    fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener)

    fun getCurrentUser(): FirebaseUser?

    fun getPriceForArtwork(category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit)

    fun savePriceForArtwork(
        category: String,
        artworkId: String,
        price: Float,
        userId: String,
    ): Task<Void>

    suspend fun deleteUserAccount(uid: String): Boolean

    suspend fun getArtworkLists(category: String): List<DomainArtwork>

    suspend fun getFavoriteArtworks(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>

}