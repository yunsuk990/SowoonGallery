package com.example.domain.model

data class DomainArtwork(
    var key: String? = null,
    var material: String? = null,
    var size: String? = null,
    var name: String? = null,
    var upload_at: String? = null,
    var category: String? = null,
    var madeIn: String? = null,
    var url: String? = null,
    var favoriteUser: Map<String, Boolean> = emptyMap(),
    var likedArtworks: Map<String, Boolean> = emptyMap(),
    var prices: Map<String, Price> = emptyMap()
)
