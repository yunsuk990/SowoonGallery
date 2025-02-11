package com.example.domain.model

data class DomainArtistProfile(
    val introduce: String = "",
    val career: Career = Career()
)

data class Career(
    val graduate: String = "",
    val awards: String = "",
    val exhibition: String = ""
)