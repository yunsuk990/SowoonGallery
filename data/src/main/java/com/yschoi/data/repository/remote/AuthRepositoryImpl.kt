package com.yschoi.data.repository.remote

import android.net.Uri
import com.yschoi.data.repository.remote.datasource.ArtworkDataSource
import com.yschoi.data.repository.remote.datasource.AuthDataSource
import com.yschoi.data.repository.remote.datasource.FirebaseDataSource
import com.yschoi.domain.model.Career
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.model.Response
import com.yschoi.domain.repository.AuthRepository
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val artworkDataSource: ArtworkDataSource,
    private val firebaseDataSource: FirebaseDataSource
): AuthRepository {
    override fun logOut() = authDataSource.logOut()
    override fun getAuthStateFlow() = authDataSource.getAuthStateFlow()
    override suspend fun saveUserInfo(user: DomainUser): Response<Boolean>{
        saveUid()
        return authDataSource.saveUserInfo(user.uid, user)
    }
    override fun saveUid() = authDataSource.saveUid()
    override fun getUid(): String? = authDataSource.getUid()
    override fun clearUid() = authDataSource.clearUid()

    override fun getMostViewedCategory(): String? = authDataSource.getMostViewedCategory()
    override fun saveRecentCategory(category: String) = authDataSource.saveRecentCategory(category)

    override fun registerMessagingToken(uid: String) = authDataSource.registerMessagingToken(uid)
    override fun registerMessagingNewToken(uid: String, token: String) = authDataSource.registerMessagingNewToken(uid = uid, token = token)

    override fun getUserInfo(uid: String): Flow<DomainUser?> = authDataSource.getUserInfo(uid)
    override suspend fun getUserInfoOnce(uid: String): DomainUser = authDataSource.getUserInfoOnce(uid)

    override suspend fun deleteUserAccount(user: DomainUser): Boolean {

        //uid 지우기
        clearUid()

        firebaseDataSource.deleteChatRoom(user.uid)

        //유저 좋아요 한 기록 삭제
        artworkDataSource.removeUserFromLikedArtworks(user.uid)

        //유저 북마크 한 기록 삭제
        artworkDataSource.removeUserFromBookmarkArtworks(user.uid)

        //유저 프로필 이미지 삭제
        if(user.profileImage.isNotEmpty()) authDataSource.removeUserProfileImage(user.profileImage)

        //유저 db 삭제
        authDataSource.deleteUserRtdb(user.uid)

        //유저 계정 삭제
        authDataSource.deleteAccount(user.uid)
        return true
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
                    return Response.Success(null)
                } else {
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
        if(updateUser.birth != currentUser.birth) updateFields["birth"] = updateUser.birth
        if(updateUser.review != currentUser.review) updateFields["review"] = updateUser.review
        if(updateUser.email != currentUser.email) updateFields["email"] = updateUser.email

        val imageUrl = if(uri != null && uri != Uri.EMPTY){
            artworkDataSource.uploadImageToStorage(name = currentUser.name, uri, mode = 1)
        }else if(uri == Uri.EMPTY){
            ""
        }else{
            null
        }
        imageUrl?.let {
            authDataSource.removeUserProfileImage(currentUser.profileImage)
            updateFields["profileImage"] = it
        }

        return if(updateFields.isNotEmpty()){
            authDataSource.updateUserProfile(getUid()!!, updateFields)

        }else{
            Response.Success(true)
        }
    }

    override suspend fun setArtistIntroduce(artistIntroduce: String): Response<Boolean> = authDataSource.setAristIntroduce(artistIntroduce)
    override suspend fun setArtistCareer(career: Career): Response<Boolean> = authDataSource.setArtistCareer(career)

}