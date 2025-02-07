package com.example.presentation.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.example.domain.usecase.SaveUserProfileImageUseCase
import com.example.presentation.model.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val saveUserProfileImageUseCase: SaveUserProfileImageUseCase
): ViewModel() {


    private val _userInfoStateFlow = MutableStateFlow<DomainUser?>(DomainUser())
    val userInfoStateFlow: StateFlow<DomainUser?> = _userInfoStateFlow

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    var uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun userInfo(user: DomainUser?){
        _userInfoStateFlow.value = user
    }

    //프로필 업데이트
    fun updateUserProfile(uri: Uri?, updateUserInfo: DomainUser) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val response = saveUserProfileImageUseCase.execute(uri, _userInfoStateFlow.value!!, updateUserInfo)
            Log.d("updateUserProfile", response.toString())
            when(response){
                is Response.Success -> {
                    _uploadState.value = UploadState.Success
                }
                is Response.Error -> {
                    _uploadState.value = UploadState.Error(response.message)
                }
                else -> { _uploadState.value = UploadState.Idle}
            }
        }
    }


    //UploadState 초기화
    fun resetUploadState(){
        _uploadState.value = UploadState.Idle
    }

}