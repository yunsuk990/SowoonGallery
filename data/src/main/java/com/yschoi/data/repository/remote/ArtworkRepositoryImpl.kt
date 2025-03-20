package com.yschoi.data.repository.remote

import android.net.Uri
import com.yschoi.data.repository.remote.datasource.ArtworkDataSource
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.Response
import com.yschoi.domain.repository.ArtworkRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtworkRepositoryImpl @Inject constructor(
    private val artworkDataSource: ArtworkDataSource
) : ArtworkRepository {
    override suspend fun getArtworkLists(): List<DomainArtwork> {
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

    override fun getLikedCountArtwork(artworkUid: String, listener: ValueEventListener) = artworkDataSource.getLikedCountArtwork(artworkUid, listener)

    override suspend fun uploadNewArtwork(artworkList: List<Pair<Uri, DomainArtwork>>): Response<Boolean>{
        return try {
            // Coroutine scope 시작
            coroutineScope {
                val uploadResults = artworkList.map { (uri, artwork) ->
                    // 각 이미지에 대해 비동기적으로 업로드 작업 실행
                    async {
                        val artworkUrl = artworkDataSource.uploadImageToStorage(name = artwork.name!!, imageUri = uri, mode = 0)
                        val updatedArtwork = artwork.copy(url = artworkUrl)
                        artworkDataSource.uploadImageToRTDB(updatedArtwork)
                    }
                }

                // 모든 업로드 작업을 병렬로 실행하고 완료 대기
                uploadResults.awaitAll()
            }

            // 모든 업로드가 성공적으로 완료된 경우
            Response.Success(true)
        } catch (e: Exception) {
            // 예외 발생 시 에러 메시지와 함께 Response.Error 반환
            Response.Error(e.message.toString(), e)
        }
    }

    override suspend fun getArtistSoldArtworks(artworksUid: Map<String, Boolean>): List<DomainArtwork>{
        var response = artworkDataSource.getArtistSoldArtworks(artworksUid)
        return response
    }

}