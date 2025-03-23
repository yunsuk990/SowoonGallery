package com.yschoi.presentation.view

import android.content.Intent
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
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.presentation.R
import com.yschoi.presentation.model.Screen
import com.yschoi.presentation.utils.AdView
import com.yschoi.presentation.utils.Banner
import com.yschoi.presentation.utils.FullScreenArtwork
import com.yschoi.presentation.utils.shimmerEffect
import com.yschoi.presentation.viewModel.MainViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val isLoggedIn by viewModel.isLoggedInState.collectAsState()
    val advertiseImageState by viewModel.advertiseImagesState.collectAsState()
    val artistRecentArtworks by viewModel.artistRecentArtworks.collectAsState()
    val isLoadingRecentArtworks by viewModel.isLoadingRecentArtworks.collectAsState()
    val isLoadingAdvertiseImages by viewModel.isLoadingAdvertiseImages.collectAsState()
    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.loadRecentArtworks(10)
    }

    HomeRoot(
        navController = navController,
        isLoggedIn = isLoggedIn,
        advertiseImageState = advertiseImageState,
        artistRecentArtworks = artistRecentArtworks,
        isLoadingRecentArtworks = isLoadingRecentArtworks,
        isLoadingAdvertiseImages = isLoadingAdvertiseImages,
        navigateToArworkDetailScreen = { index ->
            val intent = Intent(context, ArtworkDetailActivity::class.java)
            intent.putExtra("artworkId", artistRecentArtworks[index].key)
            intent.putExtra("artistUid", artistRecentArtworks[index].artistUid)
            context.startActivity(intent)
        }
    )
}

@Composable
fun HomeRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
    advertiseImageState: List<String>,
    artistRecentArtworks: List<DomainArtwork>,
    isLoadingRecentArtworks: Boolean,
    isLoadingAdvertiseImages: Boolean,
    navigateToArworkDetailScreen: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    var imageTranslate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        HomeTopBar(isLoggedIn = isLoggedIn)
        AdvertiseImages(
            advertiseImageState = advertiseImageState,
            imageLaunch = {
                imageTranslate = true
            },
            isLoadingAdvertiseImages = isLoadingAdvertiseImages
        )
        RecentArtworks(
            modifier = Modifier.padding(start = 15.dp, top = 20.dp, bottom = 20.dp),
            artistRecentArtworks = artistRecentArtworks,
            isLoadingRecentArtworks = isLoadingRecentArtworks,
            navigateToArworkDetailScreen = navigateToArworkDetailScreen
        )

        //Admob 광고
//        AdView(
//            modifier = Modifier.fillMaxWidth()
//        )
        

        Text("체험", fontSize = 18.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 20.dp), fontWeight = FontWeight.Bold)
        Banner(title = "도자기 컵 페인팅 체험", modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp), imageId = R.drawable.sowoon_banner_exp1,
            onClick = {
                navController.navigate("${Screen.Banner.route}/0") {
                    launchSingleTop = true
                    restoreState = true
                }
            })
        Banner(title = "그림 페인팅 체험", modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp), imageId = R.drawable.sowoon_banner_exp2,
            onClick = {
                navController.navigate("${Screen.Banner.route}/1") {
                    launchSingleTop = true
                    restoreState = true
                }
            })


    }
    if(imageTranslate){
        FullScreenArtwork(imageUrls = advertiseImageState, onClose = {imageTranslate = false})
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
                item(){artworkHomeCard(DomainArtwork(), modifier = Modifier
                    .width(250.dp)
                    .height(180.dp)
                    .shimmerEffect())}
                item(){artworkHomeCard(DomainArtwork(), modifier = Modifier
                    .width(180.dp)
                    .height(180.dp)
                    .shimmerEffect())}
            }
        }else{
            Text("최근 작품", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                items(artistRecentArtworks.size){ index ->
                    artworkHomeCard(
                        artwork = artistRecentArtworks[index],
                        modifier = Modifier
                            .height(180.dp)
                            .clip(RoundedCornerShape(5.dp)),
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
    Box(modifier = Modifier
        .wrapContentSize()
        .clip(RoundedCornerShape(8.dp))
        .clickable { onClick() }) {
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
    val pagerState = rememberPagerState(pageCount = {advertiseImageState.size}, initialPage = 0)
    val context = LocalContext.current

    if(isLoadingAdvertiseImages){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .shimmerEffect()){}
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
                    Box(modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(8.dp))
                }
            }
        }
    }else{
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) { page ->
            AsyncImage(
                model = advertiseImageState[page],
//                        ImageRequest.Builder(context)
//                    .data(advertiseImageState[page])
//                    .crossfade(true)
//                    .diskCachePolicy(CachePolicy.ENABLED)
//                    .memoryCachePolicy(CachePolicy.ENABLED)
//                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable {
                        imageLaunch()
                    },
                contentScale = ContentScale.Crop
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

        val animationScope = rememberCoroutineScope()

        with(pagerState){
            LaunchedEffect(pagerState.currentPage) {
                if(advertiseImageState.size > 1){
                    delay(2000L)
                    animationScope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1)%advertiseImageState.size)
                    }
                }
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
    Surface {
        Column {
            HomeRoot(
                navController = rememberNavController(),
                isLoggedIn = false,
                advertiseImageState = listOf(),
                artistRecentArtworks = listOf(DomainArtwork(likedArtworks = mapOf("a" to true)), DomainArtwork(likedArtworks = mapOf("a" to true, "b" to true))),
                isLoadingRecentArtworks = true,
                navigateToArworkDetailScreen = {},
                isLoadingAdvertiseImages = true
            )
        }
    }
}