package com.example.domain.model

sealed class Response<T>{
    data class Success<T>(val data: T): Response<T>()
    data class Error<T>(val message: String, val exception: Throwable? = null): Response<T>()
}
