package com.yschoi.presentation.model

sealed class UploadState {
    object Idle: UploadState()
    object Loading: UploadState()
    object Success: UploadState()
    data class Error(val message: String): UploadState()
}
