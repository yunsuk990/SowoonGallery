package com.example.domain.repository

import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot

interface FirebaseRepository {

    suspend fun getArtworkLists(category: String?): List<DomainArtwork>

    fun saveUserInfo(user: DomainUser): Task<Void>

    fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot>

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult>

    fun setFavoriteArtwork(): Task<Void>
    fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot>

}