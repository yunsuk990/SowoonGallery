package com.example.data.repository.remote.datasourceimpl

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.repository.remote.datasource.AuthDataSource
import com.example.domain.model.Career
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    @ApplicationContext private val context: Context
): AuthDataSource {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("users_prefs", Context.MODE_PRIVATE)
    private val usersRef = firebaseRtdb.getReference("users")

    companion object {
        private const val KEY_UID = "USER_UID"
    }

    override fun getAuthStateFlow(): Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if(auth != null){
                trySend(auth.currentUser?.uid)
            }else{
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            Log.d("getAuthStateFlow", "closed")
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.distinctUntilChanged()

    override suspend fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    override fun saveUid() {
        Log.d("saveUid",firebaseAuth.currentUser?.uid.toString())
        sharedPreferences.edit().putString(KEY_UID, firebaseAuth.currentUser?.uid.toString()).apply()
    }


    override fun getUid(): String? = sharedPreferences.getString(KEY_UID, null)

    override fun clearUid() = sharedPreferences.edit().remove(KEY_UID).apply()

    override fun signOut() {
        clearUid()
        firebaseAuth.signOut()
    }

    override fun clear() {}

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
            val result = usersRef.child(uid).removeValue().await()
            result != null
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
            Log.d("checkUserRtdbUseCase", snapshot.exists().toString())
            snapshot.exists()
        }catch (e: Exception){
            Log.d("checkUserRtdbUseCase", e.toString())
            false
        }
    }

    //로그인 처리
    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Response<AuthResult?> {
        return try {
            Log.d("signInWithPhoneAuthCredential", "called" )
            val response = firebaseAuth.signInWithCredential(credential).await()
            Response.Success(response)
        }catch (e: Exception){
            Log.d("signInWithPhoneAuthCredential", "exception: ${e.message}" )
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
}