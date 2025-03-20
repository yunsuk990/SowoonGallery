package com.yschoi.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.model.Response
import com.yschoi.domain.usecase.SaveUserProfileImageUseCase
import com.yschoi.domain.usecase.authUseCase.DeleteAccountUseCase
import com.yschoi.presentation.model.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val saveUserProfileImageUseCase: SaveUserProfileImageUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
): ViewModel() {

    private val _userInfoStateFlow = MutableStateFlow<DomainUser>(DomainUser())
    val userInfoStateFlow: StateFlow<DomainUser> = _userInfoStateFlow

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    var uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun userInfo(user: DomainUser){
        _userInfoStateFlow.value = user
    }

    fun updateUserProfile(updateUserInfo: DomainUser) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val response = saveUserProfileImageUseCase.execute(null, _userInfoStateFlow.value!!, updateUserInfo)
            when(response){
                is Response.Success -> {
                    _uploadState.value = UploadState.Success
                }
                is Response.Error -> {
                    _uploadState.value = UploadState.Error(response.message)
                }
                else -> { _uploadState.value = UploadState.Idle }
            }
        }
    }

    fun signOut(user: DomainUser = userInfoStateFlow.value){
        viewModelScope.launch {
            deleteAccountUseCase.execute(user)
        }
    }

}