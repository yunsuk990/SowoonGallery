package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.domain.usecase.authUseCase.GetUserInfoUseCase
import com.example.domain.usecase.artworkUseCase.*
import com.example.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtworkViewModel @Inject constructor(
    private val getFavoriteArtworkUseCase: GetFavoriteArtworkUseCase,
    private val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase,
    private val getLikedArtworkUseCase: GetLikedArtworkUseCase,
    private val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    private val getLikedCountArtworkUsecase: GetLikedCountArtworkUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val getArtworksUseCase: GetArtworksUseCase,
): ViewModel() {

    private val _artworkFavoriteState = MutableLiveData<Boolean>()
    var artworkFavoriteState: LiveData<Boolean> = _artworkFavoriteState

    private val _artworkLikedState = MutableLiveData<Boolean>()
    var artworkLikedState: LiveData<Boolean> = _artworkLikedState

    private val _artworkLikedCountState = MutableLiveData<Int>()
    var artworkLikedCountState: LiveData<Int> = _artworkLikedCountState

    var userUid: String? = getCurrentUserUidUseCase.execute()

    private val _artistInfo =  MutableStateFlow(DomainUser())
    val artistInfo: StateFlow<DomainUser> = _artistInfo.asStateFlow()

    private val _artistArtworks =  MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artistArtworks: StateFlow<List<DomainArtwork>> = _artistArtworks.asStateFlow()

    private val _userInfo = MutableStateFlow(DomainUser())
    val userInfo: StateFlow<DomainUser> = _userInfo


    init {
        getUserInfo(userUid)
    }

    fun getUserInfo(uid: String? = userUid){
        uid?.let {
            viewModelScope.launch {
               _userInfo.value = getUserInfoUseCase.excuteOnce(uid)
            }
        }
    }

    fun getFavoriteArtwork(artworkUid: String){
        userUid?.let {
            getFavoriteArtworkUseCase.execute(it, artworkUid) { isFavorite ->
                _artworkFavoriteState.value = isFavorite
            }
        }
    }

    fun getArtistInfo(uid: String) = viewModelScope.launch {
        _artistInfo.value = getUserInfoUseCase.excuteOnce(uid)
    }

    fun setFavoriteArtwork(isFavorite: Boolean, artworkUid: String,) {
        userUid?.let {
            setFavoriteArtworkUseCase.execute(it, artworkUid, !isFavorite){ isFavorite ->
                _artworkFavoriteState.value = isFavorite
            }
        }
    }

    fun getLikedArtwork(artworkUid: String){
        userUid?.let {
            getLikedArtworkUseCase.execute(it, artworkUid){ isLiked ->
                _artworkLikedState.value = isLiked
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

    fun getLikedCountArtwork(artworkUid: String, category: String){
        getLikedCountArtworkUsecase.execute(artworkUid, category){ countVal ->
            _artworkLikedCountState.value = countVal
        }
    }

    fun getArtistArtworks(artistUid: String) {
        viewModelScope.launch {
            var result = getArtworksUseCase.executeByUid(artistUid)
            _artistArtworks.value = result
            Log.d("getArtistArtworks_viewModel", result.toString())
        }
    }


    //판매 확정하기
    fun confirmArtworkSold(key: String) {

    }
}