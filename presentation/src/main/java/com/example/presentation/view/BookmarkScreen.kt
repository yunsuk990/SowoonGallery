package com.example.presentation.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun BookMarkScreen(viewModel: MainViewModel, navController: NavController) {
    val favoriteItem by viewModel.artworkFavoriteLiveData.collectAsState()
    LaunchedEffect(key1 = favoriteItem){
        viewModel.getFavoriteArtworksList()
    }
    Column {
        BookMarkScreenTopBar(navController)
        artworkGridLayout(artworkList = favoriteItem)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarkScreenTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(text = "BookMark", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기")
            }
        },
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun testFavorite(){

}
