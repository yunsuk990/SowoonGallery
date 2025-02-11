package com.example.domain.model

data class DomainUser(
    var uid: String = "",
    var name: String = "",
    var sex: Int = 0, //0:남자
    var age: Int = 0,
    var birth: String = "",
    var profileImage: String = "",
    var mode: Int = 0,  //Artist:1 또는 User:0 또는 Manager:2
    var review: String = "",
    var artistProfile: DomainArtistProfile = DomainArtistProfile(),
    var artworksUid: Map<String, Boolean> = emptyMap(),
    var purchasedArtworks: Map<String, Boolean> = emptyMap(),
    var selledArtworks: Map<String, Boolean> = emptyMap(),
    var favoriteArtworks: Map<String, Boolean> = emptyMap(),
    var likedArtworks: Map<String, Boolean> = emptyMap(),
)