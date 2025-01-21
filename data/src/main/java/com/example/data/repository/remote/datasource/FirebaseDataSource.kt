package com.example.data.repository.remote.datasource

import com.example.data.model.DataUser
import com.example.domain.model.DomainArtwork
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {

    //작품 전체 가져오기
    suspend fun getAllArtworks(): List<DomainArtwork>

    //카테고리별 작품 가져오기
    suspend fun getArtworksByCategory(category: String): List<DomainArtwork>

    fun savePriceForArtwork(category: String, artworkId: String, price: Float, userId: String): Task<Void>



    //사용자 가입 처리
    fun saveUserInfo(uid: String, user: DataUser): Task<Void>

    //사용자 가입 여부 확인
    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    //sms 인증처리
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean, category: String): Task<Void>

    fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void>

    fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener)

    suspend fun deleteAccount(uid: String): Boolean

    suspend fun deleteUserRtdb(uid: String): Boolean

    suspend fun removeUserFromLikedArtworks(uid: String): Boolean

    suspend fun getFavoritesArtwork(uid: String): Flow<List<DomainArtwork>>

    suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>>


    //전적 가져오기
    //fun getScore(): Task<QuerySnapshot>

    //전적 저장하기
    //fun setScore(score: DataScore): Task<Void>
}