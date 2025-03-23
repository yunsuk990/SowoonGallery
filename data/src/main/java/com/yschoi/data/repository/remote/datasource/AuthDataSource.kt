package com.yschoi.data.repository.remote.datasource

import com.yschoi.domain.model.Career
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.model.Response
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthDataSource{
    //로그아웃
    fun logOut()

    fun getAuthStateFlow(): Flow<String?>

    //사용자 가입 처리
    suspend fun saveUserInfo(uid: String, user: DomainUser): Response<Boolean>

    //사용자 정보 가져오기
    fun getUserInfo(uid: String): Flow<DomainUser?>

    //사용자 가입 여부 확인
    suspend fun checkUserRtdbUseCase(uid: String): Boolean

    //sms 인증처리
    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<AuthResult?>

    //회원탈퇴 - 회원계정 삭제
    suspend fun deleteAccount(uid: String): Boolean

    //회원탈퇴 - DB 삭제
    suspend fun deleteUserRtdb(uid: String): Boolean

    //유저 UID 저장
    fun saveUid()

    //유저 UID 불러오기
    fun getUid(): String?

    //유저 UID 삭제
    fun clearUid()

    //최근 본 작품의 카테고리 저장
    fun saveRecentCategory(category: String)

    //최근 본 작품의 카테고리 불러오가
    fun getRecentCategoryData(): Map<String, Int>

    //최근 가장 많이 본 작품의 카테고리 불러오기
    fun getMostViewedCategory(): String?

    //최근 본 작품 정보 삭제
    fun deleteRecentCategory()

    //Messaging - 기기 토큰 등록
    fun registerMessagingToken(uid: String)

    fun registerMessagingNewToken(uid: String = getUid()!!, token: String)

    suspend fun getUserInfoOnce(uid: String): Flow<DomainUser>

    //프로필 이미지 url Realtime Database에 저장하기
    suspend fun updateUserProfile(uid: String, updateUserProfile: Map<String, Any>): Response<Boolean>

    //작가 프로필 소개 정보 저장
    suspend fun setAristIntroduce(artistIntroduce: String):Response<Boolean>

    //작가 프로필 커리어 정보 저장
    suspend fun setArtistCareer(career: Career):Response<Boolean>

    // 유저 프로필 이미지 삭제
    suspend fun removeUserProfileImage(imageUrl: String): Response<Boolean>

}