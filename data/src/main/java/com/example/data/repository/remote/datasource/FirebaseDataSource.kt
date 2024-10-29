package com.example.data.repository.remote.datasource

import com.example.data.model.DataUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot

interface FirebaseDataSource {

    //작품 전체 가져오기
    fun getArtworkLists(uid: String): Task<DataSnapshot>

    fun saveUserInfo(uid: String, user: DataUser): Task<Void>

    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>


    //전적 가져오기
    //fun getScore(): Task<QuerySnapshot>

    //전적 저장하기
    //fun setScore(score: DataScore): Task<Void>
}