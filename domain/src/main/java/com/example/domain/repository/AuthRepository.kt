package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.Career
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun logOut()
    fun clear()
    fun getAuthStateFlow(): Flow<String?>
    //사용자 가입 처리
    suspend fun saveUserInfo(user: DomainUser): Response<Boolean>
    //사용자 정보 가져오기
    fun getUserInfo(uid: String): Flow<DomainUser?>
    suspend fun getUserInfoOnce(uid: String): DomainUser
    //사용자 가입 여부 확인
    suspend fun checkUserRtdbUseCase(uid: String): Boolean
    //sms 인증처리
    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<String?>
    suspend fun deleteUserAccount(uid: String): Boolean
    suspend fun getCurrentUid(): String?

    fun saveUid()
    fun getUid(): String?
    fun clearUid()
    fun getMostViewedCategory(): String?
    fun saveRecentCategory(category: String)

    fun registerMessagingToken(uid: String)

    suspend fun updateProfileInfo(
        uri: Uri?,
        currentUser: DomainUser,
        updateUser: DomainUser
    ): Response<Boolean>

    suspend fun setArtistIntroduce(artistIntroduce: String): Response<Boolean>
    suspend fun setArtistCareer(career: Career): Response<Boolean>
}