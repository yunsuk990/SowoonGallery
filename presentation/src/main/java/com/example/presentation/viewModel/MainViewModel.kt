package com.example.presentation.viewModel

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainArtwork
import com.example.domain.usecase.DeleteAccountUseCase
import com.example.domain.usecase.GetArtworksUseCase
import com.example.domain.usecase.GetFavoriteArtworksUseCase
import com.example.domain.usecase.GetLikedArtworksUseCase
import com.example.presentation.utils.CustomDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArtworksUseCase: GetArtworksUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val favoriteArtworksUseCase: GetFavoriteArtworksUseCase,
    private val likeArtworksUseCase: GetLikedArtworksUseCase
): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    var isLoggedInState = mutableStateOf(false)
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        Log.d("authStateListener", auth.currentUser.toString())
        isLoggedInState.value = auth.currentUser != null
    }

    private var _artworkLiveData =  MutableLiveData<List<DomainArtwork>>()
    var artworkLiveData: LiveData<List<DomainArtwork>> =  _artworkLiveData

    private var _artworkFavoriteLiveData =  MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkFavoriteLiveData: StateFlow<List<DomainArtwork>> =  _artworkFavoriteLiveData

    private var _artworkLikedLiveData =  MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkLikedLiveData: StateFlow<List<DomainArtwork>> =  _artworkLikedLiveData

    init {
        isLoggedInState.value = auth.currentUser != null
        auth.addAuthStateListener(authStateListener)
        viewModelScope.launch {
            _artworkLiveData.value = getArtworksUseCase.execute(null)
        }
    }


    fun getArworksList(category: String?) = viewModelScope.launch { _artworkLiveData.value = getArtworksUseCase.execute(category) }

    fun getFavoriteArtworksList() {
        if(auth.currentUser == null) return
        viewModelScope.launch {
            favoriteArtworksUseCase.execute(auth.uid!!)
                .catch { exception -> Log.d("getFavoriteArtworksList", "Error ${exception.message}") }
                .collect { artworkList ->
                    Log.d("getFavoriteArtworksList", artworkList.toString())
                    _artworkFavoriteLiveData.value = artworkList
                }
        }
    }

    fun getLikedArtworksList(){
        if(auth.currentUser == null) return
        viewModelScope.launch {
            likeArtworksUseCase.execute(auth.uid!!)
                .catch { exception -> Log.d("getLikedArtworksList", "Error ${exception.message}") }
                .collect { artworkList ->
                    Log.d("getLikedArtworksList", artworkList.toString())
                    _artworkLikedLiveData.value = artworkList
                }
        }
    }


    fun logOut(){
        auth.signOut()

    }

    fun deleteAccount(){
        if(auth.currentUser == null) return
        viewModelScope.launch {
            auth.uid?.let {
                deleteAccountUseCase.execute(auth.uid!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}