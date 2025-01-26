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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.DomainArtwork
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedScreen(viewModel: MainViewModel, navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val likedItem by viewModel.artworkLikedLiveData.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = likedItem){
        viewModel.getLikedArtworksList()
    }
    Column {
        LikedTopBar(scrollBehavior = scrollBehavior)
        likedContent(likedItem, viewModel, context)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Favorites", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {},
        actions = {},
        scrollBehavior = scrollBehavior,
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}
@Composable
fun likedContent(likedItem: List<DomainArtwork>?, viewModel: MainViewModel,  context: Context) {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement =  Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                likedItem?.let { item ->
                items(likedItem.size){
                    artworkCard(artwork = likedItem!![it], viewModel){
                        context.startActivity(Intent(context, ArtworkActivity::class.java).apply {
                            putExtra("artwork", Gson().toJson(likedItem!![it]))
                        })
                    }
                }
            }
        })
    }
}