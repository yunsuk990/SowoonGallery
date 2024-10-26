package com.example.presentation.model

import android.os.Parcelable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.versionedparcelable.ParcelField
import com.example.presentation.R
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Artwork(
    var title: String,
    var date: Date = Date(),
    var image: Int
): Parcelable
