package com.example.data.repository

import com.example.data.mapper.MainMapper
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.domain.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
): FirebaseRepository {
    override suspend fun getArtworkLists(category: String?): List<DomainArtwork> {
        return firebaseDataSource.getArtworkLists(category)
    }

    override fun saveUserInfo(user: DomainUser) = firebaseDataSource.saveUserInfo(user.uid, MainMapper.userMapper(user))

    override fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot> = firebaseDataSource.checkUserRtdbUseCase(uid)

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult> = firebaseDataSource.signInWithPhoneAuthCredential(credential)
    override fun setFavoriteArtwork(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
        category: String
    ): Task<Void> = firebaseDataSource.setFavoriteArtwork(uid, artworkUid, isFavorite, category)

    override fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = firebaseDataSource.getFavoriteArtwork(uid, artworkUid)
    override fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void> = firebaseDataSource.setLikedArtwork(uid, artworkUid, isLiked, category)

    override fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = firebaseDataSource.getLikedArtwork(uid,artworkUid)
    override fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener) = firebaseDataSource.getLikedCountArtwork(artworkUid, category, listener)

}