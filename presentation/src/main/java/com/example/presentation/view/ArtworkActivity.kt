package com.example.presentation.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.utils.FullScreenArtwork
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ArtworkViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )
            SowoonTheme {
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
    val artworkLikedCountState by viewModel.artworkLikedCountState.observeAsState(0)
    var isZoomDialogOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)){
        Column(
            modifier = Modifier.verticalScroll(scrollstate)
        ){
            ArtworkTopBar(
                modifier = Modifier.background(Color.White),
                artworkFavoriteState,
            ) { viewModel.setFavoriteArtwork(artworkFavoriteState, artwork.key!!, artwork.category!!) }
            AsyncImage(
                model = artwork.url,
                contentDescription = "이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 10f)
                    .clickable { isZoomDialogOpen = true },
                contentScale = ContentScale.Crop
            )
            artworkInfo(artwork, artworkLikedState){
                viewModel.setLikedArtwork(artworkLikedState, artwork.key!!, artwork.category!!)
            }
            Spacer(modifier = Modifier.height(50.dp))
            artworkMenu()
            if (isZoomDialogOpen) {
                FullScreenArtwork(
                    imageUrl = artwork.url!!,
                    onClose = { isZoomDialogOpen = false }
                )
            }
        }

        userActionButton(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            artwork,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTopBar(modifier: Modifier, favoriteState: Boolean, favoriteBtnOnClick: () -> Unit){
    var context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text( text = "Sowoon", style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = {
                (context as Activity).finish()
            }) { Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기") }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        actions = {
            IconButton(onClick = { favoriteBtnOnClick() }) {
                if(favoriteState){
                    Icon(painterResource(R.drawable.bookmark_filled), contentDescription = "저장", modifier = Modifier.size(30.dp), tint = Color.Black)
                }else{
                    Icon(painterResource(R.drawable.bookmark_border), contentDescription = "저장", modifier = Modifier.size(30.dp), tint = Color.Black)
                }
            }
        },
        modifier = modifier
    )
}

@Preview(showSystemUi = true)
@Composable
fun artworkActivityTest(){
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "소운",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(painter = painterResource(id = R.drawable.back), contentDescription = null) }
                },
                contentColor = Color.Black,
                backgroundColor = Color.White,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(painterResource(R.drawable.bookmark_filled), contentDescription = "저장", modifier = Modifier.size(30.dp))
                    }
                },
            )
        }
    }
}

@Composable
fun userActionButton(
    modifier: Modifier,
    artwork: DomainArtwork,
) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {

        Row(
            modifier = Modifier.padding(20.dp)
        ) {
            Column {
                Text(text = "판매중", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "1,300,000원", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    context.startActivity(Intent(context, ArtworkPriceActivity::class.java).putExtra(
                        "artwork", Gson().toJson(artwork, DomainArtwork::class.java)
                    ))
                },
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(0.5.dp,color=Color.Black),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Text(text = "가격 제시하기", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun userMakePrice(){
    val artwork = DomainArtwork()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
            Text(text = artwork.name!!, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = artwork.madeIn.toString(), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = artwork.material.toString(), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = artwork.size!!, fontSize = 16.sp)
        }
    }
}

@Composable
fun artworkInfo(artwork: DomainArtwork,  likedState: Boolean, onClick: () -> Unit){
    Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp).background(Color.White)) {
        Row {
            Column {
                Text(text = artwork.name!!, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = artwork.madeIn.toString(), fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 5.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            artworkLikeBtn(likedState = likedState) {
                onClick()
            }
        }
        Divider(thickness = 0.5.dp, color = Color.LightGray, modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))
        Text(text = artwork.material.toString(), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = artwork.size!!, fontSize = 14.sp)
    }
}

@Composable
fun artworkLikeBtn(
    likedState: Boolean,
    likedBtnOnClick: () -> Unit,
){

    IconButton(
        onClick = { likedBtnOnClick() }
    ) {
        if (!likedState) {
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = "좋아요",
                modifier = Modifier.size(30.dp)
            )
        } else {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "좋아요",
                modifier = Modifier.size(30.dp),
                tint = Color.Red

            )
        }
    }
}

@Composable
fun artworkMenu() {
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
        .background(Color.White)
        .padding(10.dp)
        .height(150.dp)) {
        Text(text = "작품 리뷰")
    }
}

@Composable
fun differentArtworks() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(10.dp)
            .height(150.dp)
    ) {
        Text("Profile Screen")
    }
}