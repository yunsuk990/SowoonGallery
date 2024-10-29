package com.example.data.repository.remote.datasourceimpl

import com.example.data.model.DataUser
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
    private val firestore: FirebaseStorage
): FirebaseDataSource {
    override fun getArtworkLists(uid: String): Task<DataSnapshot> {
        return  firebaseRtdb.getReference("users").child(uid).get()
    }

    override fun saveUserInfo(uid: String, user: DataUser): Task<Void> {
        return firebaseRtdb.getReference("users").child(uid).setValue(user)
    }

    override fun checkUserRtdbUseCase(uid: String): Task<DataSnapshot> {
        return firebaseRtdb.getReference("users").child(uid).get()
    }

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }
}