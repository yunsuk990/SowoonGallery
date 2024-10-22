package com.example.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector){
    object Home : Screen("home", Icons.Filled.Home)
    object Favorites : Screen("favorites", Icons.Filled.Favorite)
    object Profile : Screen("profile", Icons.Filled.Person)
}
