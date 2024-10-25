package com.example.presentation.model

sealed class AuthState{
    object Authenticated : AuthState()
    object ExistUser : AuthState()
    data class NewUser(val uid: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object Loading : AuthState()
}