package com.example.data.repository.remote

import android.net.Uri
import android.util.Log
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.data.repository.remote.datasource.AuthDataSource
import com.example.domain.model.Career
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.example.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val artworkDataSource: ArtworkDataSource
): AuthRepository {
    override fun signOut() = authDataSource.signOut()
    override fun clear() = authDataSource.clear()
    override fun getAuthStateFlow() = authDataSource.getAuthStateFlow()
    override suspend fun saveUserInfo(user: DomainUser): Response<Boolean>{
        saveUid()
        return authDataSource.saveUserInfo(user.uid, user)
    }
    override suspend fun getCurrentUid() = authDataSource.getCurrentUid()
    override fun saveUid() = authDataSource.saveUid()
    override fun getUid(): String? = authDataSource.getUid()
    override fun clearUid() = authDataSource.clearUid()

    override fun getUserInfo(uid: String): Flow<DomainUser?> = authDataSource.getUserInfo(uid)
    override suspend fun getUserInfoOnce(uid: String): DomainUser = authDataSource.getUserInfoOnce(uid)

    override suspend fun deleteUserAccount(uid: String): Boolean {
//        val artworkDeleted = firebaseDataSource.removeUserFromLikedArtworks(uid)
//        if (artworkDeleted) {
//            val userDeleted = firebaseDataSource.deleteUserRtdb(uid)
//            if (userDeleted) {
//                return firebaseDataSource.deleteAccount(uid)
//            }
//        }
        return false
    }
    override suspend fun checkUserRtdbUseCase(uid: String): Boolean = authDataSource.checkUserRtdbUseCase(uid)


    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<String?> {
        var authResult = authDataSource.signInWithPhoneAuthCredential(credential)
        when(authResult){
            is Response.Success -> {
                val userUid = authResult.data?.user?.uid.toString()
                val isRegistered = checkUserRtdbUseCase(userUid)
                if (isRegistered) {
                    authDataSource.saveUid()
                    Log.d("signInWithPhoneAuthCredential", "isRegistered_기존 사용자")
                    return Response.Success(null)
                } else {
                    Log.d("signInWithPhoneAuthCredential", "isRegistered_새 사용자")
                    return Response.Success(userUid)
                }
            }
            is Response.Error -> {
                return Response.Error(exception = authResult.exception, message = authResult.message)
            }
        }
    }

    override suspend fun updateProfileInfo(
        uri: Uri?,
        currentUser: DomainUser,
        updateUser: DomainUser
    ): Response<Boolean>{

        val updateFields = mutableMapOf<String, Any>()
        if(updateUser.name != currentUser.name) updateFields["name"] = updateUser.name
        if(updateUser.age != currentUser.age) updateFields["age"] = updateUser.age
        if(updateUser.review != currentUser.review) updateFields["review"] = updateUser.review

        val imageUrl = if(uri != null){
            artworkDataSource.uploadImageToStorage(uri)
        }else{
            null
        }
        imageUrl?.let {
            updateFields["profileImage"] = it
        }

        return if(updateFields.isNotEmpty()){
            Log.d("uploadProfileImage_Repository", updateFields.toString())
            authDataSource.updateUserProfile(getUid()!!, updateFields)
        }else{
            Log.d("uploadProfileImage_Repository", updateFields.toString())
            Response.Success(true)
        }
    }

    override suspend fun setArtistIntroduce(artistIntroduce: String): Response<Boolean> = authDataSource.setAristIntroduce(artistIntroduce)
    override suspend fun setArtistCareer(career: Career): Response<Boolean> = authDataSource.setArtistCareer(career)

}