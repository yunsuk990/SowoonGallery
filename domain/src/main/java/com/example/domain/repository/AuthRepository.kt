package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signOut()
    fun clear()
    fun getAuthStateFlow(): Flow<String?>
    //사용자 가입 처리
    fun saveUserInfo(user: DomainUser): Task<Void>
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

    suspend fun updateProfileInfo(
        uri: Uri?,
        currentUser: DomainUser,
        updateUser: DomainUser
    ): Response<Boolean>
}