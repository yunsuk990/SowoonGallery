package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    var sold: Boolean = false,
    var minimalPrice: Int = 0,
    var currentPrice: Int = minimalPrice
): Parcelable