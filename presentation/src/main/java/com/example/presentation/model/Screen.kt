package com.example.presentation.model

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.presentation.R

sealed class Screen(val route: String, val icon: ImageVector){
    object Home : Screen("home", Icons.Filled.Home)
    object Favorite : Screen("favorites", Icons.Filled.Favorite)
    object BookMark: Screen("bookmark", Icons.Filled.ShoppingCart)
    object Profile : Screen("profile", Icons.Filled.Person)
    object Setting : Screen("setting", Icons.Filled.Settings)
}
