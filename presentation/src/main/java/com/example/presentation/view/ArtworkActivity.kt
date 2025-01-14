package com.example.presentation.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.ui.theme.SowoonGalleryTheme
import com.example.presentation.viewModel.ArtworkViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtworkActivity : ComponentActivity() {
    private val viewModel: ArtworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val argument = intent.getStringExtra("artwork")
        val artwork: DomainArtwork = Gson().fromJson(argument, DomainArtwork::class.java)

        // artwork.key가 변경될 때마다 실행됩니다.
        viewModel.getFavoriteArtwork(artworkUid = artwork.key!!)
        viewModel.getLikedArtwork(artworkUid = artwork.key!!)
        viewModel.getLikedCountArtwork(artwork.key!!, artwork.category!!)


        setContent {
            SowoonGalleryTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    ArtworkScreen(artwork, viewModel)
                }
            }
        }
    }
}

@Composable
fun ArtworkScreen(artwork: DomainArtwork, viewModel: ArtworkViewModel){
    val scrollstate = rememberScrollState()
    val artworkFavoriteState by viewModel.artworkFavoriteState.observeAsState(initial = false)
    val artworkLikedState by viewModel.artworkLikedState.observeAsState(initial = false)
    val artworkLikedCountState by viewModel.artworkLikedCountState.observeAsState("")

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)){
        ArtworkTopBar(
            modifier = Modifier.zIndex(1f),
            artworkFavoriteState,
        ) { viewModel.setFavoriteArtwork(artworkFavoriteState, artwork.key!!, artwork.category!!) }
        Column(
            modifier = Modifier
                .verticalScroll(scrollstate)
                .padding(top = 65.dp, bottom = 75.dp)
        ){
            AsyncImage(
                model = artwork.url,
                contentDescription = "이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 10f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),

                ) {
                Text(text = artwork.name!!, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = artwork.madeIn.toString(), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = artwork.material.toString(), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = artwork.size!!, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(50.dp))
            artworkMenu()
        }

        userActionButton(
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .align(Alignment.BottomCenter),
            artworkLikedState,
            artworkLikedCountState
        ){ viewModel.setLikedArtwork(artworkLikedState, artwork.key!!, artwork.category!!) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTopBar(modifier: Modifier, favoriteState: Boolean, favoriteBtnOnClick: () -> Unit){
    var context = LocalContext.current

    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = { IconButton(onClick = {
            (context as Activity).finish()
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
        }},
        actions = {
            IconButton(onClick = { favoriteBtnOnClick() }) {
                if(favoriteState){
                    Icon(painterResource(R.drawable.bookmark_filled), contentDescription = "저장", modifier = Modifier.size(30.dp))
                }else{
                    Icon(painterResource(R.drawable.bookmark_border), contentDescription = "저장", modifier = Modifier.size(30.dp))
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun userActionButton(
    modifier: Modifier,
    likedState: Boolean,
    artworkLikedCountState: Any,
    likedBtnOnClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = { likedBtnOnClick() }, modifier = Modifier
            .background(colorResource(id = R.color.test))
            .padding(7.dp)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Log.d("userActionButton", likedState.toString())
                if(!likedState) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = "좋아요", modifier = Modifier.size(30.dp))
                } else {
                    Icon(Icons.Filled.Favorite, contentDescription = "좋아요", modifier = Modifier.size(30.dp))
                }
                Text(text = artworkLikedCountState.toString())
            }
        }

        TextButton(
            modifier = Modifier.weight(1f),
            onClick = {}) {
            Text(text = "구매상담", fontSize = 16.sp, color = Color.Black)
        }

    }
}
@Preview(showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun artworkMenu(){
    var selectedTabIdx by remember { mutableStateOf(0) }
    val tabTitles = listOf("작품리뷰", "비슷한 작품 ")
    Column {
        TabRow(
            selectedTabIndex = selectedTabIdx,
            backgroundColor = Color.White,
            indicator = {position ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(position[selectedTabIdx]),
                    color = Color.Gray // 밑줄 색상 변경
                )
            },
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, color = if(selectedTabIdx==index) Color.Black else Color.Gray) }
                )
            }
        }
        when(selectedTabIdx){
            0 -> menuReview()
            1 -> differentArtworks()
        }
    }
}

@Composable
fun menuReview(){
    Column(modifier = Modifier
        .padding(10.dp)
        .height(150.dp)) {
        Text(text = "작품 리뷰")
    }
}

@Composable
fun differentArtworks(){
    Column(modifier = Modifier
        .padding(10.dp)
        .height(150.dp)) {
        Text("Profile Screen")
    }
}

