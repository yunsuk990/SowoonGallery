package com.yschoi.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.Response
import com.yschoi.domain.usecase.artworkUseCase.UploadNewArtworkUseCase
import com.yschoi.domain.usecase.authUseCase.GetCurrentUserUidUseCase
import com.yschoi.presentation.model.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtworkUploadViewModel @Inject constructor(
    private val uploadNewArtworkUseCase: UploadNewArtworkUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
): ViewModel() {

    private var _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private var _userUid = MutableStateFlow<String?>(null)
    val userUid: StateFlow<String?> = _userUid

    init {
       viewModelScope.launch {  _userUid.value = getCurrentUserUidUseCase.execute() }
    }

    fun uploadNewArtwork(artworkList: List<Pair<Uri, DomainArtwork>>){
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val response = uploadNewArtworkUseCase.executeList(artworkList)
            when(response){
                is Response.Success -> { _uploadState.value = UploadState.Success }
                is Response.Error -> { _uploadState.value = UploadState.Error(response.message) }
            }
        }
    }
}