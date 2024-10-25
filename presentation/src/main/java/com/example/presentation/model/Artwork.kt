package com.example.presentation.model

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.presentation.R
import java.util.Date

data class Artwork(
    var title: String,
    var date: Date = Date(),
    var image: Int
)
