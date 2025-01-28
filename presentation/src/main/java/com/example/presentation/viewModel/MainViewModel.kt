package com.example.presentation.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.example.domain.usecase.DeleteAccountUseCase
import com.example.domain.usecase.GetArtworksUseCase
import com.example.domain.usecase.GetFavoriteArtworksUseCase
import com.example.domain.usecase.GetLikedArtworksUseCase
import com.example.domain.usecase.GetUserInfoUseCase
import com.example.domain.usecase.SaveUserProfileImageUseCase
import com.example.domain.usecase.SetLikedArtworkUseCase
import com.example.presentation.model.ArtworkSort
import com.example.presentation.model.UploadState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getArtworksUseCase: GetArtworksUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val favoriteArtworksUseCase: GetFavoriteArtworksUseCase,
    private val likeArtworksUseCase: GetLikedArtworksUseCase,
    private val setLikedArtworkUseCase: SetLikedArtworkUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserProfileImageUseCase: SaveUserProfileImageUseCase
): ViewModel() {

    val auth: FirebaseAuth = Firebase.auth
    var isLoggedInState = mutableStateOf(false)
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val currentUser = auth.currentUser
        Log.d("authStateListener", currentUser.toString())
        isLoggedInState.value = currentUser != null
        currentUser?.let { user ->
            getUserInfoUseCase.execute(currentUser.uid){ response ->
                when(response){
                    is Response.Success -> {
                        Log.d("authStateListener", response.data.toString())
                        _userInfoStateFlow.value = response.data
                    }

                    is Response.Error -> {
                        Log.d("authStateListener", response.message)
                    }
                }
            }
        }
    }
    //User 정보
    private var _userInfoStateFlow = MutableStateFlow<DomainUser>(DomainUser())
    val userInfoStateFlow: StateFlow<DomainUser> = _userInfoStateFlow


    private var _artworkLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    val artworkLiveData: StateFlow<List<DomainArtwork>> = _artworkLiveData.asStateFlow()

    private var _artworkFavoriteLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkFavoriteLiveData: StateFlow<List<DomainArtwork>> = _artworkFavoriteLiveData

    private var _artworkLikedLiveData = MutableStateFlow<List<DomainArtwork>>(emptyList())
    var artworkLikedLiveData: StateFlow<List<DomainArtwork>> = _artworkLikedLiveData

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    var uploadState: StateFlow<UploadState> = _uploadState

    init {
        isLoggedInState.value = auth.currentUser != null
        auth.addAuthStateListener(authStateListener)

        // 카테고리 작품들 가져오기
        loadArtworks("전체")
    }

    // 카테고리 작품들 가져오기
    fun loadArtworks(category: String) = viewModelScope.launch {
        getArtworksUseCase.execute(category).collect { artworkList ->
            Log.d("loadArtworks", artworkList.toString())
            _artworkLiveData.value = artworkList
        }
    }

    //프로필 업데이트
    fun updateUserProfile(uid: String= _userInfoStateFlow.value.uid , uri: Uri?, name: String, age: Int) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val response = saveUserProfileImageUseCase.execute(uid, uri, _userInfoStateFlow.value, name, age)
            when(response){
                is Response.Success -> {
                    _uploadState.value = UploadState.Success
                }
                is Response.Error -> {
                    _uploadState.value = UploadState.Error(response.message)
                }
            }
        }
    }

    //UploadState 초기화
    fun resetUploadState(){
        _uploadState.value = UploadState.Idle
    }


    //작품 정렬
    fun sortArtworks(sortBy: ArtworkSort, category: String){
        Log.d("sortArtworks", _artworkLiveData.value.toString())
        when(sortBy){
            ArtworkSort.NONE -> loadArtworks(category)
            ArtworkSort.BOOKMARK -> _artworkLiveData.value = _artworkLiveData.value.sortedByDescending { it.favoriteUser.size }
            ArtworkSort.DATE -> _artworkLiveData.value = _artworkLiveData.value.sortedBy { it.upload_at }
            ArtworkSort.LIKE -> _artworkLiveData.value = _artworkLiveData.value.sortedByDescending { it.likedArtworks.size }
        }
        Log.d("sortArtworks", _artworkLiveData.value.toString())
    }


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