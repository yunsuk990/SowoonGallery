package com.example.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainViewModel: ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    var isLoggedInState = mutableStateOf(false)
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        Log.d("authStateListener", auth.currentUser.toString())
        isLoggedInState.value = auth.currentUser != null
    }

    init {
        isLoggedInState.value = auth.currentUser != null
        auth.addAuthStateListener(authStateListener)
    }

    fun logOut(){
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}