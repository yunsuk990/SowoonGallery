package com.yschoi.data.repository.remote.datasourceimpl

import android.content.Context
import android.content.SharedPreferences
import com.yschoi.data.repository.remote.datasource.AuthDataSource
import com.yschoi.domain.model.Career
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.model.Response
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseMessaging: FirebaseMessaging,
    @ApplicationContext private val context: Context
): AuthDataSource {


    private val profileStorageRef = firebaseStorage.getReference("profiles")

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("users_prefs", Context.MODE_PRIVATE)
    private val usersRef = firebaseRtdb.getReference("users")

    companion object {
        private const val KEY_UID = "USER_UID"
        private const val KEY_RECENT_CATEGORY = "RECENT_CATEGORY"
    }

    override fun getAuthStateFlow(): Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

    override fun saveUid() {
        sharedPreferences.edit().putString(KEY_UID, firebaseAuth.currentUser?.uid.toString()).apply()
    }

    override fun saveRecentCategory(category: String) {
        val categoryMap = getRecentCategoryData().toMutableMap()
        var flag = false
        categoryMap[category] = categoryMap.getOrDefault(category, 0) + 1
        for(value in categoryMap.values){
            if(value <= 10) flag = true
        }
        if(!flag){
            for((key, value) in categoryMap){
                categoryMap[key] = value % 10
            }
        }
        sharedPreferences.edit().putString(KEY_RECENT_CATEGORY, Gson().toJson(categoryMap)).apply()
    }

    override fun getRecentCategoryData(): Map<String, Int>{
        val json = sharedPreferences.getString(KEY_RECENT_CATEGORY, "")
        return Gson().fromJson(json, object : TypeToken<Map<String, Int>>() {}.type) ?: emptyMap()
    }

    override fun getMostViewedCategory(): String? {
        val categoryMap = getRecentCategoryData()
        return categoryMap.maxByOrNull { it.value }?.key
    }

    override fun deleteRecentCategory(){
        sharedPreferences.edit().remove(KEY_RECENT_CATEGORY).apply()
    }

    override fun getUid(): String?{
        val uid = sharedPreferences.getString(KEY_UID, null)
        return uid

    }

    override fun clearUid(){
        sharedPreferences.edit().remove(KEY_UID).apply()
    }

    override fun registerMessagingToken(uid: String) {
        firebaseMessaging.token.addOnCompleteListener{
            var token = HashMap<String, Any>()
            token.put("pushToken", it.result)
            usersRef.child(uid).updateChildren(token)
        }
    }

    override fun registerMessagingNewToken(uid: String, token: String) {
        var token = HashMap<String, Any>()
        token.put("pushToken", token)
        usersRef.child(uid).updateChildren(token)
    }

    override fun logOut() {
        clearUid()
        deleteRecentCategory()
        firebaseAuth.signOut()
    }

    // Firebase Auth에서 사용자 삭제
    override suspend fun deleteAccount(uid: String): Boolean {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            true
        }catch (e: Exception) {
            false
        }
    }

    // 사용자 데이터 삭제
    override suspend fun deleteUserRtdb(uid: String): Boolean {
        return try {
            usersRef.child(uid).removeValue().await()
            true
        }catch (e: Exception){
            false
        }
    }

    override suspend fun updateUserProfile(
        uid: String,
        updateFields: Map<String, Any>
    ): Response<Boolean> {
        return try {
            usersRef.child(uid).updateChildren(updateFields).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error("Failed to update user profile", e)
        }
    }

    // 사용자 정보를 db에 저장
    override suspend fun saveUserInfo(uid: String, user: DomainUser): Response<Boolean> {
        return try {
            usersRef.child(uid).setValue(user).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error(message = e.message.toString(), exception = e)
        }
    }

    override suspend fun getUserInfoOnce(uid: String): DomainUser {
        val snapshot = usersRef.child(uid).get().await()
        return snapshot.getValue(DomainUser::class.java)!!
    }

    override fun getUserInfo(uid: String): Flow<DomainUser?> = callbackFlow {
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userInfo = snapshot.getValue(DomainUser::class.java)
                if( userInfo != null){
                    trySend(userInfo)
                }else{
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        usersRef.child(uid).addValueEventListener(valueEventListener)
        awaitClose{
            usersRef.removeEventListener(valueEventListener)
        }
    }

    // 등록된 사용자인지 확인
    override suspend fun checkUserRtdbUseCase(uid: String): Boolean{
        return try {
            val snapshot = usersRef.child(uid).get().await()
            snapshot.exists()
        }catch (e: Exception){
            false
        }
    }

    //로그인 처리
    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<AuthResult?> {
        return try {
            val response = firebaseAuth.signInWithCredential(credential).await()
            Response.Success(response)
        }catch (e: Exception){
            Response.Error(message =  e.message.toString(), exception = e)
        }
    }

    //작가 소개 글 update
    override suspend fun setAristIntroduce(artistIntroduce: String): Response<Boolean> {
        return try {
            var uid = getUid()
            usersRef.child(uid!!).child("artistProfile").updateChildren(
                mapOf(
                    "introduce" to artistIntroduce
                )
            ).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error(e.message.toString(), e)
        }
    }

    override suspend fun setArtistCareer(career: Career): Response<Boolean> {
        return try {
            var uid = getUid()
            usersRef.child(uid!!).child("artistProfile").child("career").updateChildren(
                mapOf(
                    "graduate" to career.graduate,
                    "awards" to career.awards,
                    "exhibition" to career.exhibition
                )
            ).await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error(e.message.toString(), e)
        }
    }

    override suspend fun removeUserProfileImage(imageUrl: String): Response<Boolean> {
        return try {
            val response = profileStorageRef.storage.getReferenceFromUrl(imageUrl).delete().await()
            Response.Success(true)
        }catch (e: Exception){
            Response.Error(exception = e, message = e.message.toString())
        }
    }
}