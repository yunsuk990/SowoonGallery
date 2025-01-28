package com.example.domain.model

data class DomainUser(
    var uid: String = "",
    var name: String = "",
    var age: Int = 0,
    var profileImage: String = "",
    var favoriteArtworks: Map<String, String> = emptyMap(),
    var likedArtworks: Map<String, String> = emptyMap(),
){

}
