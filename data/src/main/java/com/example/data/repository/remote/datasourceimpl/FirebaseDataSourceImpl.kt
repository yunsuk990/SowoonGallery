package com.example.data.repository.remote.datasourceimpl

import android.util.Log
import com.example.data.model.DataUser
import com.example.data.repository.remote.datasource.FirebaseDataSource
import com.example.domain.model.DomainArtwork
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRtdb: FirebaseDatabase,
        private val firestore: FirebaseStorage
): FirebaseDataSource {
    override suspend fun getArtworkLists(category: String?): List<DomainArtwork> {
        val ref = if(category != null){
            firebaseRtdb.getReference("images").child(category)
        }else{
            firebaseRtdb.getReference("images")
        }
        val snapshot = ref.get().await()

        val itemList = mutableListOf<DomainArtwork>()

        if(category != null){
            snapshot?.children?.forEach{ artworkSnapshot ->
                Log.d("GetArtworksUseCase_null", artworkSnapshot.key.toString())
                Log.d("GetArtworksUseCase_null!", artworkSnapshot.value.toString())
                val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
                artwork!!.key = artworkSnapshot.key
                Log.d("arwork_null_artwork", artwork.toString())
                if(artwork != null){
                    itemList.add(artwork)
                }
            }
        }else{
            // 카테고리가 없을 때
            snapshot?.children?.forEach { categorySnapshot ->
                categorySnapshot.children.forEach { artworkSnapshot ->
                    val artwork = artworkSnapshot.getValue(DomainArtwork::class.java)
                    artwork!!.key = artworkSnapshot.key
                    if (artwork != null) {
                        Log.d("GetArtworksUseCase_null", artwork.toString())
                        itemList.add(artwork)
                    }
                }
            }
        }
        return itemList
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

    override fun setFavoriteArtwork(
        uid: String,
        artworkUid: String,
        isFavorite: Boolean,
        category: String
    ): Task<Void> {
        return if(!isFavorite){
            firebaseRtdb.getReference("users").child(uid).child("favoriteArtworks").child(artworkUid).removeValue()
            firebaseRtdb.getReference("images").child(category).child(artworkUid).child("favoriteUser").child(uid).removeValue()
        }else{
            firebaseRtdb.getReference("users").child(uid).child("favoriteArtworks").child(artworkUid).setValue(isFavorite)
            firebaseRtdb.getReference("images").child(category).child(artworkUid).child("favoriteUser").child(uid).setValue(isFavorite)
        }
    }

    override fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot> {
        return firebaseRtdb.getReference("users").child(uid).child("favoriteArtworks").child(artworkUid).get()
    }

    override fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean, category: String): Task<Void> {
        return if(!isLiked){
            firebaseRtdb.getReference("users").child(uid).child("likedArtworks").child(artworkUid).removeValue()
            firebaseRtdb.getReference("images").child(category).child(artworkUid).child("likedArtworks").child(uid).removeValue()
        }else{
            firebaseRtdb.getReference("users").child(uid).child("likedArtworks").child(artworkUid).setValue(isLiked)
            firebaseRtdb.getReference("images").child(category).child(artworkUid).child("likedArtworks").child(uid).setValue(isLiked)
        }
    }

    override fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot> {
        return firebaseRtdb.getReference("users").child(uid).child("likedArtworks").child(artworkUid).get()
    }

    override fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener) {
        firebaseRtdb.getReference("images").child(category).child(artworkUid).child("likedArtworks").addValueEventListener(listener)
    }
}