package com.example.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.presentation.R
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.viewModel.MainViewModel

@Composable
fun LikedScreen(viewModel: MainViewModel, navController: NavController) {
    val likedItem by viewModel.artworkLikedLiveData.collectAsState()
    LaunchedEffect(key1 = likedItem){
        viewModel.getLikedArtworksList()
    }
    Column {
        LikedTopBar(navController)
        artworkGridLayout(artworkList = likedItem)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedTopBar(navController: NavController) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    CenterAlignedTopAppBar(
        title = { Text(text = "Favorites", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.noRippleClickable {
                    navController.popBackStack()
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기")
            }
        },
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}
