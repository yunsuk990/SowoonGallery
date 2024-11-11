package com.example.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.usecase.GetFavoriteArtworkUseCase
import com.example.domain.usecase.SetFavoriteArtworkUseCase
import com.example.presentation.model.AuthState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtworkViewModel @Inject constructor(
    private val getFavoriteArtworkUseCase: GetFavoriteArtworkUseCase,
    private val setFavoriteArtworkUseCase: SetFavoriteArtworkUseCase
): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    //
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _artworkLikedState = MutableLiveData<Boolean>()
    var artworkLikedState: LiveData<Boolean> = _artworkLikedState

    fun getFavoriteArtwork(uid: String? = auth.uid, artworkUid: String){
        getFavoriteArtworkUseCase.execute(uid!!, artworkUid).observeForever{liked ->
            _artworkLikedState.value = liked
        }
    }

    fun setFavoriteArtwork(artworkLikedState: Boolean?) {
        if(auth == null){
            _authState.value = AuthState.Error("로그인하세요.")
            return
        }
        setFavoriteArtworkUseCase.execute(uid)
    }


}