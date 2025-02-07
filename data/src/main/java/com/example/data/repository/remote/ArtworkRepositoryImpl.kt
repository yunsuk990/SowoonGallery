package com.example.data.repository.remote

import android.net.Uri
import android.util.Log
import com.example.data.repository.remote.datasource.ArtworkDataSource
import com.example.domain.model.DomainArtwork
import com.example.domain.model.PriceWithUser
import com.example.domain.model.Response
import com.example.domain.repository.ArtworkRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtworkRepositoryImpl @Inject constructor(
    private val artworkDataSource: ArtworkDataSource
) : ArtworkRepository {
    override suspend fun getArtworkLists(): List<DomainArtwork> {
//        return if(category == "전체"){
//            Log.d("getArtworkLists_Repository", "category==전체")
//            artworkDataSource.getAllArtworks()
//        }else{
//            Log.d("getArtworkLists_Repository", "category==$category")
//            artworkDataSource.getArtworksByCategory(category)
//        }
        return artworkDataSource.getAllArtworks()
    }

    override suspend fun getFavoriteArtworks(uid: String): Flow<List<DomainArtwork>> {
        return artworkDataSource.getFavoritesArtwork(uid)
    }


    override suspend fun getRecentArtworks(limit: Int): List<DomainArtwork> = artworkDataSource.getRecentArtworks(limit)

    override suspend fun getArtistArtworks(artistId: String): List<DomainArtwork> = artworkDataSource.getArtistArtworks(artistId)

    override suspend fun getArtworkById(artworkId: String): DomainArtwork = artworkDataSource.getArtworkById(artworkId)

    override suspend fun getLikedArtworks(uid: String): Flow<List<DomainArtwork>> = artworkDataSource.getLikedArtworks(uid)

    override fun setFavoriteArtwork(uid: String, artworkUid: String, isFavorite: Boolean): Task<Void> = artworkDataSource.setFavoriteArtwork(uid, artworkUid, isFavorite)

    override fun getFavoriteArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = artworkDataSource.getFavoriteArtwork(uid, artworkUid)

    override fun setLikedArtwork(uid: String, artworkUid: String, isLiked: Boolean): Task<Void> = artworkDataSource.setLikedArtwork(uid, artworkUid, isLiked)

    override fun getLikedArtwork(uid: String, artworkUid: String): Task<DataSnapshot> = artworkDataSource.getLikedArtwork(uid,artworkUid)

    override fun getLikedCountArtwork(artworkUid: String, category: String, listener: ValueEventListener) = artworkDataSource.getLikedCountArtwork(artworkUid, category, listener)

    override suspend fun uploadNewArtwork(artwork: DomainArtwork, imageUri: Uri): Response<Boolean> {
        //artworkUrl -> Storage에 업로드
        val artworkUrl = artworkDataSource.uploadImageToStorage(imageUri)
        val updateArtwork = artwork.copy(url = artworkUrl)
        return artworkDataSource.uploadImageToRTDB(updateArtwork)
    }

}