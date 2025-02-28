package com.example.data.repository.remote.datasource

import com.example.domain.model.Career
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow

interface AuthDataSource{
    fun signOut()
    fun clear()
    fun getAuthStateFlow(): Flow<String?>
    //사용자 가입 처리
    suspend fun saveUserInfo(uid: String, user: DomainUser): Response<Boolean>
    //사용자 정보 가져오기
    fun getUserInfo(uid: String): Flow<DomainUser?>
    //사용자 가입 여부 확인
    suspend fun checkUserRtdbUseCase(uid: String): Boolean
    //sms 인증처리
    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<AuthResult?>
    suspend fun deleteAccount(uid: String): Boolean
    suspend fun deleteUserRtdb(uid: String): Boolean
    suspend fun getCurrentUid(): String?

    fun saveUid()
    fun getUid(): String?
    fun clearUid()

    fun registerMessagingToken(uid: String)

    suspend fun getUserInfoOnce(uid: String): DomainUser

    //프로필 이미지 url Realtime Database에 저장하기
    suspend fun updateUserProfile(uid: String, updateUserProfile: Map<String, Any>): Response<Boolean>

    suspend fun setAristIntroduce(artistIntroduce: String):Response<Boolean>

    suspend fun setArtistCareer(career: Career):Response<Boolean>

    suspend fun removeUserProfileImage(imageUrl: String): Response<Boolean>


}