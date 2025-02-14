package com.example.presentation.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.utils.ArtInfo
import com.example.presentation.utils.FullScreenArtwork
import com.example.presentation.utils.shimmerEffect
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    val advertiseImageState by viewModel.advertiseImagesState.collectAsState()
    var imageTranslate by remember { mutableStateOf(false) }
    val artistRecentArtworks by viewModel.artistRecentArtworks.collectAsState()
    val isLoadingRecentArtworks by viewModel.isLoadingRecentArtworks.collectAsState()
    val isLoadingAdvertiseImages by viewModel.isLoadingAdvertiseImages.collectAsState()
    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.loadRecentArtworks(10)
    }

    HomeRoot(
        isLoggedIn = isLoggedIn,
        advertiseImageState = advertiseImageState,
        imageTranslate = imageTranslate,
        artistRecentArtworks = artistRecentArtworks,
        isLoadingRecentArtworks = isLoadingRecentArtworks,
        isLoadingAdvertiseImages = isLoadingAdvertiseImages,
        imageDismiss = {imageTranslate = false},
        imageLaunch = { imageTranslate = true},
        navigateToArworkDetailScreen = { index ->
            var intent = Intent(context, ArtworkDetailActivity::class.java).putExtra("artwork", Gson().toJson(artistRecentArtworks[index]))
            intent.putExtra("userInfo", Gson().toJson(userInfo))
            context.startActivity(intent)
        }
    )
}

@Composable
fun HomeRoot(
    isLoggedIn: Boolean,
    advertiseImageState: List<String>,
    imageTranslate: Boolean,
    artistRecentArtworks: List<DomainArtwork>,
    isLoadingRecentArtworks: Boolean,
    isLoadingAdvertiseImages: Boolean,
    imageDismiss: () -> Unit,
    imageLaunch: () -> Unit,
    navigateToArworkDetailScreen: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        HomeTopBar(isLoggedIn = isLoggedIn)
        AdvertiseImages(advertiseImageState = advertiseImageState, imageLaunch, isLoadingAdvertiseImages)
        RecentArtworks(
            modifier = Modifier.padding(start = 15.dp, top = 20.dp, bottom = 20.dp),
            artistRecentArtworks = artistRecentArtworks,
            isLoadingRecentArtworks = isLoadingRecentArtworks,
            navigateToArworkDetailScreen = navigateToArworkDetailScreen
        )
    }
    if(imageTranslate){
        FullScreenArtwork(imageUrl = R.drawable.sowoon_bg) {
            imageDismiss()
        }
    }

}

@Composable
fun RecentArtworks(
    modifier: Modifier,
    artistRecentArtworks: List<DomainArtwork>,
    isLoadingRecentArtworks: Boolean,
    navigateToArworkDetailScreen: (Int) -> Unit
) {
    Column(modifier = modifier) {
        if(isLoadingRecentArtworks){
            Text("최근 작품", fontSize = 18.sp, color = Color.Transparent, modifier = Modifier.shimmerEffect(), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                item(){artworkHomeCard(DomainArtwork(), modifier = Modifier.width(250.dp).height(180.dp).shimmerEffect())}
                item(){artworkHomeCard(DomainArtwork(), modifier = Modifier.width(180.dp).height(180.dp).shimmerEffect())}
            }
        }else{
            Text("최근 작품", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                items(artistRecentArtworks.size){ index ->
                    artworkHomeCard(
                        artwork = artistRecentArtworks[index],
                        modifier = Modifier.height(180.dp).clip(RoundedCornerShape(5.dp)),
                        onClick = { navigateToArworkDetailScreen(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun artworkHomeCard(artwork: DomainArtwork, modifier: Modifier, onClick: () -> Unit = {}) {
    var imageWidth by remember { mutableStateOf(0) }
    Column(modifier = Modifier.wrapContentSize().clickable { onClick() }) {
        AsyncImage(
            model = artwork.url,
            modifier = modifier.onGloballyPositioned { coordinates ->
                imageWidth = coordinates.size.width },
            contentDescription = null,
        )
    }
}

@Composable
fun AdvertiseImages(advertiseImageState: List<String>, imageLaunch: () -> Unit, isLoadingAdvertiseImages: Boolean) {
    val pagerState = rememberPagerState(pageCount = {advertiseImageState.size})
    val context = LocalContext.current

    if(isLoadingAdvertiseImages){
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).shimmerEffect()){}
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .shimmerEffect(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    Box(modifier = Modifier.padding(2.dp).clip(CircleShape).size(8.dp))
                }
            }
        }
    }else{
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        ) { page ->
//      Log.d("AsyncImages", advertiseImageState[page])
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(advertiseImageState[page])
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable {
                        imageLaunch()
                    },
                contentScale = ContentScale.FillHeight
            )
        }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(advertiseImageState.size) { iteration ->
                Log.d("repeat", "called")
                val color = if(pagerState.currentPage == iteration) Color.Black else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(isLoggedIn: Boolean){
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text(text = "Sowoon", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {},
        actions = {
            if(!isLoggedIn){
                IconButton(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { context.startActivity(
                        Intent(context, StartActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY))}
                ){
                    Icon(Icons.Filled.Person, contentDescription = null, Modifier.size(30.dp))
                }
            }
        },
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}


@Preview(showBackground = true)
@Composable
fun HomeScreenTest(){
    Scaffold {
        Column {
            HomeRoot(
                isLoggedIn = false,
                advertiseImageState = listOf(),
                imageTranslate = false,
                artistRecentArtworks = listOf(DomainArtwork(likedArtworks = mapOf("a" to true)), DomainArtwork(likedArtworks = mapOf("a" to true, "b" to true))),
                isLoadingRecentArtworks = true,
                imageDismiss = {},
                imageLaunch = {},
                navigateToArworkDetailScreen = {},
                isLoadingAdvertiseImages = true
            )
        }
    }
}