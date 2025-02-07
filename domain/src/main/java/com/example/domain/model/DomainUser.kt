package com.example.domain.model

data class DomainUser(
    var uid: String = "",
    var name: String = "",
    var age: Int = 0,
    var profileImage: String = "",
    var mode: Int = 0,  //Artist:1 또는 User:0
    var review: String = "",
    var artworksUid: Map<String, Boolean> = emptyMap(),
    var favoriteArtworks: Map<String, Boolean> = emptyMap(),
    var likedArtworks: Map<String, Boolean> = emptyMap(),
)