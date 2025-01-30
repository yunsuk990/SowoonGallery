package com.example.domain.model

data class DomainArtwork(
    var key: String? = "",
    var artistUid: String? = "",
    var material: String? = "",
    var size: String? = "",
    var name: String? = "",
    var upload_at: String? = "",
    var category: String? = "",
    var madeIn: String? = "",
    var url: String? = "",
    var review: String? = "",
    var favoriteUser: Map<String, Boolean> = emptyMap(),
    var likedArtworks: Map<String, Boolean> = emptyMap(),
    var prices: Map<String, DomainPrice> = emptyMap(),
    var sold: Boolean = false,
    var minimalPrice: Float = 0f,
    var currentPrice: Float = minimalPrice
)
