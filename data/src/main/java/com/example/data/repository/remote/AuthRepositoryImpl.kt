package com.example.data.repository.remote

import android.net.Uri
import android.util.Log
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.data.repository.remote.datasource.AuthDataSource
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
    override fun saveUserInfo(user: DomainUser): Task<Void>{
        saveUid()
        return authDataSource.saveUserInfo(user.uid, user)
    }
    override suspend fun getCurrentUid() = authDataSource.getCurrentUid()
    override fun saveUid() = authDataSource.saveUid()
    override fun getUid(): String? = authDataSource.getUid()
    override fun clearUid() = authDataSource.clearUid()

    override fun getUserInfo(uid: String): Flow<DomainUser?> = authDataSource.getUserInfo(uid)
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
        var result = authDataSource.signInWithPhoneAuthCredential(credential)
        return if (result != null) {
            val userUid = result.user?.uid.toString()
            val isRegistered = checkUserRtdbUseCase(userUid)
            if (isRegistered) {
                authDataSource.saveUid()
                Log.d("signInWithPhoneAuthCredential", "isRegistered_기존 사용자")
                Response.Success(null)
            } else {
                Log.d("signInWithPhoneAuthCredential", "isRegistered_새 사용자")
                Response.Success(userUid)
            }
        } else {
            Response.Error("인증 실패")
        }

//        result.addOnSuccessListener {
//            if(userSnapshot.result.exists()){
//                Log.d("signInWithPhoneAuthCredential", "checkUserRtdbUseCase_exists")
//            }else{
//                Log.d("signInWithPhoneAuthCredential", "checkUserRtdbUseCase_Noexists")
//            }
//        }.addOnFailureListener{
//            Log.d("signInWithPhoneAuthCredential", "Failure")
//        }
//        Log.d("result" ,result.toString())
//        authDataSource.saveUid()
//        return result
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

}