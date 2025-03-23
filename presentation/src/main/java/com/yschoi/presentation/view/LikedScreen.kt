package com.yschoi.presentation.view

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.presentation.R
import com.yschoi.presentation.utils.noRippleClickable
import com.yschoi.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun LikedScreen(viewModel: MainViewModel, navController: NavController) {
    val likedItem by viewModel.artworkLikedLiveData.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = likedItem){
        viewModel.getLikedArtworksList()
    }

    LikedRoot(
        navController = navController,
        likedItem = likedItem,
        artworkOnClick = { artwork ->
            context.startActivity(Intent(context, ArtworkDetailActivity::class.java)
                .putExtra("artworkId", artwork.key)
                .putExtra("artistUid", artwork.artistUid)
            )
        }
    )
}

@Composable
fun LikedRoot(navController: NavController, likedItem: List<DomainArtwork>, artworkOnClick: (DomainArtwork) -> Unit) {
    Column {
        LikedTopBar(navController)
        Text("${likedItem.size} 개 작품 :", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp))
        Divider(thickness = 1.dp, color = Color.LightGray)
        BookMarkLikedGridLayout(item = likedItem, artworkOnClick = artworkOnClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Favorites", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun likedScreenTest(){
    LikedRoot(
        navController = rememberNavController(),
        likedItem = listOf(
            DomainArtwork(name = "asdfasdf", minimalPrice = "10"),
            DomainArtwork(name = "asdfasdf", minimalPrice = "10"),
            DomainArtwork(name = "asdfasdf", minimalPrice = "10")),
        artworkOnClick = { artwork -> }
    )
}
