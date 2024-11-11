package com.example.data.repository.remote.datasource

import com.example.data.model.DataUser
import com.example.domain.model.DomainArtwork
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot

interface FirebaseDataSource {

    //작품 전체 가져오기
    suspend fun getArtworkLists(category: String?): List<DomainArtwork>

    //사용자 가입 처리
    fun saveUserInfo(uid: String, user: DataUser): Task<Void>

    //사용자 가입 여부 확인
    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    //sms 인증처리
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun setFavoriteArtwork(): Task<Void>
    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

    //전적 가져오기
    //fun getScore(): Task<QuerySnapshot>

    //전적 저장하기
    //fun setScore(score: DataScore): Task<Void>
}