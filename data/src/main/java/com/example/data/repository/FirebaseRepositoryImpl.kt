package com.example.data.repository

import android.net.Uri
import android.util.Log
import com.example.data.mapper.MainMapper
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.example.domain.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
): FirebaseRepository {

    override suspend fun getArtworkLists(category: String): List<DomainArtwork> {
        return if(category == "전체"){
            firebaseDataSource.getAllArtworks()
        }else{
            firebaseDataSource.getArtworksByCategory(category)
        }
    }

    override suspend fun getFavoriteArtworks(uid: String): Flow<List<DomainArtwork>> {
        return firebaseDataSource.getFavoritesArtwork(uid)
    }

    override fun getPriceForArtwork(category: String, artworkId: String, callback: (List<PriceWithUser>) -> Unit){
        firebaseDataSource.getPriceForArtwork(category,artworkId) { item ->
            Log.d("FirebaseRepository_getPriceForArtwork", item.toString())
            callback(item)
        }
    }

    override suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>> = firebaseDataSource.getLikedArtworks(uid)

    override fun saveUserInfo(user: DomainUser) = firebaseDataSource.saveUserInfo(user.uid, MainMapper.userMapper(user))
    override fun getUserInfo(uid: String, callback: (Response<DomainUser>) -> Unit) = firebaseDataSource.getUserInfo(uid,callback)
    override suspend fun uploadProfileImage(
        uid: String,
        uri: Uri?,
        currentUser: DomainUser,
        name: String,
        age: Int
    ): Response<Boolean>{

        val updateFields = mutableMapOf<String, Any>()
        if(name != currentUser.name) updateFields["name"] = name
        if(age != currentUser.age) updateFields["age"] = age
        val imageUrl = if(uri != null){
            val result = firebaseDataSource.uploadImageToStorage(uid, uri)
            when(result){
                is Response.Success -> result.data
                is Response.Error -> return Response.Error(result.message, result.exception)
            }
        }else{
            null
        }
        imageUrl?.let {
            updateFields["profileImage"] = it
        }

        return if(updateFields.isNotEmpty()){
            Log.d("uploadProfileImage_Repository", updateFields.toString())
            firebaseDataSource.updateUserProfile(uid, updateFields)
        }else{
            Log.d("uploadProfileImage_Repository", updateFields.toString())
            Response.Success(true)
        }
    }

    override fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot> = firebaseDataSource.checkUserRtdbUseCase(uid)
    override suspend fun getUserInfoLists(
        uid: List<String>,
    ) = firebaseDataSource.getUserInfoLists(uid)

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
    override fun getCurrentUser(): FirebaseUser? = firebaseDataSource.getCurrentUser()

    override fun savePriceForArtwork(
        category: String,
        artworkId: String,
        price: Float,
        userId: String,
    ): Task<Void> = firebaseDataSource.savePriceForArtwork(category,artworkId,price,userId)

    override suspend fun deleteUserAccount(uid: String): Boolean {
        val artworkDeleted = firebaseDataSource.removeUserFromLikedArtworks(uid)
        if (artworkDeleted) {
            val userDeleted = firebaseDataSource.deleteUserRtdb(uid)
            if (userDeleted) {
                return firebaseDataSource.deleteAccount(uid)
            }
        }
        return false
    }
}