package com.yschoi.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yschoi.domain.model.Career
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.model.Response
import com.yschoi.domain.usecase.authUseCase.GetUserInfoUseCase
import com.yschoi.domain.usecase.artworkUseCase.*
import com.yschoi.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import com.yschoi.presentation.model.ArtworkSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtworkViewModel @Inject constructor(
    private val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase,
    private val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getArtworksUseCase: GetArtworksUseCase,
    private val setArtistProfileUseCase: SetArtistProfileUseCase,
    private val fetchArtworkUseCase: FetchArtworkUseCase,
    private val deleteArtworkUseCase: DeleteArtworkUseCase
): ViewModel() {

    var userUid: String? = getCurrentUserUidUseCase.execute()

    private val _artwork = MutableStateFlow<DomainArtwork>(DomainArtwork())
    var artwork: StateFlow<DomainArtwork> = _artwork

    private val _artworkFavoriteState = MutableLiveData<Boolean>()
    var artworkFavoriteState: LiveData<Boolean> = _artworkFavoriteState

    private val _artworkLikedState = MutableLiveData<Boolean>()
    var artworkLikedState: LiveData<Boolean> = _artworkLikedState

    private val _artworkLikedCountState = MutableLiveData<Int>()
    var artworkLikedCountState: LiveData<Int> = _artworkLikedCountState

    private val _artistInfo =  MutableStateFlow(DomainUser())
    val artistInfo: StateFlow<DomainUser> = _artistInfo.asStateFlow()

    private val _artistArtworks =  MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artistArtworks: StateFlow<List<DomainArtwork>> = _artistArtworks.asStateFlow()

    private val _isLoadingArtistArtworks = MutableStateFlow<Boolean>(true)
    val isLoadingArtistArtworks: StateFlow<Boolean> = _isLoadingArtistArtworks

    fun fetchArtwork(artworkId: String) = viewModelScope.launch {
        fetchArtworkUseCase.fetchArtwork(artworkId).collect { artwork ->
            _artwork.value = artwork
            _artworkFavoriteState.value = artwork.favoriteUser.containsKey(userUid)
            _artworkLikedState.value = artwork.likedArtworks.containsKey(userUid)
            _artworkLikedCountState.value = artwork.likedArtworks.size
            Log.d("fetchArtwork_viewModel" ,artwork.toString())
        }
    }

    fun setData(artistArtwork: List<DomainArtwork>){
        _artistArtworks.value = artistArtwork
    }

    fun filterArtworks(category: ArtworkSort, artistArtwork: List<DomainArtwork>) {
        when(category){
            ArtworkSort.NONE -> { _artistArtworks.value = artistArtwork}
            ArtworkSort.BOOKMARK -> _artistArtworks.value = _artistArtworks.value.sortedByDescending { it.favoriteUser.size }
            ArtworkSort.DATE -> _artistArtworks.value = _artistArtworks.value.sortedBy { it.upload_at }
            ArtworkSort.LIKE -> _artistArtworks.value = _artistArtworks.value.sortedByDescending { it.likedArtworks.size }
            ArtworkSort.PRICE -> _artistArtworks.value = _artistArtworks.value.sortedByDescending { it.minimalPrice }
        }

    }

    fun deleteArtwork(artworkId: String, uid: String, category: String, imageUrl: String) = viewModelScope.launch {
        deleteArtworkUseCase.execute(artworkId, uid, category, imageUrl)
    }

    fun updateArtistProfile(artistIntroduce: String) = viewModelScope.launch {
        val response = setArtistProfileUseCase.executeArtistIntroduce(artistIntroduce)
        when(response){
            is Response.Success -> { }
            is Response.Error -> { }
        }
    }

    fun updateArtistProfile(career: Career) = viewModelScope.launch {
        val response = setArtistProfileUseCase.executeArtistCareer(career)
        when(response){
            is Response.Success -> {  }
            is Response.Error -> { }
        }
    }

    fun getArtistInfo(uid: String) = viewModelScope.launch {
        getUserInfoUseCase.excuteOnce(uid).collect{ domainUser ->
            _artistInfo.value = domainUser
        }
    }

    fun setFavoriteArtwork(isFavorite: Boolean, artworkUid: String,) {
        userUid?.let {
            setFavoriteArtworkUseCase.execute(it, artworkUid, !isFavorite){ isFavorite ->
                _artworkFavoriteState.value = isFavorite
            }
        }
    }

    fun setLikedArtwork(isLiked: Boolean, artworkUid: String){
        userUid?.let {
            setLikedArtworkUseCase.execute(it, artworkUid, !isLiked){ isLiked ->
                _artworkLikedState.value = isLiked
            }
        }
    }

    fun getArtistArtworks(artistUid: String) = viewModelScope.launch {
        _isLoadingArtistArtworks.value = true
        getArtworksUseCase.executeByUid(artistUid).collect { artworkList ->
            _artistArtworks.value = artworkList
            _isLoadingArtistArtworks.value = false
        }
    }


    //판매 확정하기
    fun setArtworkState(artistUid: String, sold: Boolean, artworkId: String, destUid: String?){
//        artistUid.let {
//            setArtworkStateUseCase.execute(artistUid = artistUid, sold = sold, artworkId = artworkId, destUid = destUid)
//        }
    }
}