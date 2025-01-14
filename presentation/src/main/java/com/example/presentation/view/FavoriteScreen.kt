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
fun BookMarkScreen(viewModel: MainViewModel, navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val favoriteItem by viewModel.artworkFavoriteLiveData.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = favoriteItem){
        viewModel.getFavoriteArtworksList()
    }
    Column {
        BookMarkScreenTopBar(scrollBehavior = scrollBehavior)
        BookMarkContent(favoriteItem, context)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarkScreenTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = { Text(text = "BookMark", textAlign = TextAlign.Center) },
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
fun BookMarkContent(bookMarkItem: List<DomainArtwork>?, context: Context) {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement =  Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                bookMarkItem?.let { item ->
                items(bookMarkItem.size){
                    artworkCard(artwork = bookMarkItem!![it]){
                        context.startActivity(Intent(context, ArtworkActivity::class.java).apply {
                            putExtra("artwork", Gson().toJson(bookMarkItem!![it]))
                        })
                    }
                }
            }
        })
    }
}


@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun testFavorite(){

}
