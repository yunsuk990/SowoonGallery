package com.example.domain.repository

import com.example.domain.model.DomainUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot

interface FirebaseRepository {

    fun getArtworkLists(uid: String): Task<DataSnapshot>

    fun saveUserInfo(user: DomainUser): Task<Void>

    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

}