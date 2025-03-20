package com.yschoi.presentation.model

sealed class AuthState{
    object Idle : AuthState()
    object ExistUser : AuthState()
    data class NewUser(val uid: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object Loading : AuthState()
}