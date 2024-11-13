package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.usecase.GetFavoriteArtworkUseCase
import com.example.domain.usecase.GetLikedArtworkUseCase
import com.example.domain.usecase.GetLikedCountArtworkUsecase
import com.example.domain.usecase.SetFavoriteArtworkUseCase
import com.example.domain.usecase.SetLikedArtworkUseCase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtworkViewModel @Inject constructor(
    private val getFavoriteArtworkUseCase: GetFavoriteArtworkUseCase,
    private val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase,
    private val getLikedArtworkUseCase: GetLikedArtworkUseCase,
    private val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    private val getLikedCountArtworkUsecase: GetLikedCountArtworkUsecase
): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _artworkFavoriteState = MutableLiveData<Boolean>()
    var artworkFavoriteState: LiveData<Boolean> = _artworkFavoriteState

    private val _artworkLikedState = MutableLiveData<Boolean>()
    var artworkLikedState: LiveData<Boolean> = _artworkLikedState

    private val _artworkLikedCountState = MutableLiveData<Int>()
    var artworkLikedCountState: LiveData<Int> = _artworkLikedCountState

    fun getFavoriteArtwork(artworkUid: String){
        getFavoriteArtworkUseCase.execute(auth.uid!!, artworkUid){ isFavorite ->
            _artworkFavoriteState.value = isFavorite
        }
    }

    fun setFavoriteArtwork(isFavorite: Boolean, artworkUid: String, category: String) {
        Log.d("isFavorite", isFavorite.toString())
//        if(auth == null){
//            _authState.value = AuthState.Error("로그인하세요.")
//            return
//        }
        setFavoriteArtworkUseCase.execute(auth.uid!!, artworkUid, !isFavorite, category){ isFavorite ->
            _artworkFavoriteState.value = isFavorite
        }
    }

    fun getLikedArtwork(artworkUid: String){
        Log.d("getLikedArtwork", artworkUid.toString())
        getLikedArtworkUseCase.execute(auth.uid!!, artworkUid){ isLiked ->
            _artworkLikedState.value = isLiked
        }
    }

    fun setLikedArtwork(isLiked: Boolean, artworkUid: String, category: String){
        Log.d("setLikedArtwork", isLiked.toString())
        setLikedArtworkUseCase.execute(auth.uid!!, artworkUid, !isLiked, category){ isLiked ->
            _artworkLikedState.value = isLiked
        }
    }

    fun getLikedCountArtwork(artworkUid: String, category: String){
        getLikedCountArtworkUsecase.execute(artworkUid, category){ countVal ->
            _artworkLikedCountState.value = countVal
        }
    }


}