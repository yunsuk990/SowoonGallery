package com.example.presentation.view

import android.app.Activity
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.utils.AutoResizedText
import com.example.presentation.utils.FullScreenArtwork
import com.example.presentation.utils.LoginToastMessage
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.utils.shimmerEffect
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ArtworkViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtworkDetailActivity : ComponentActivity() {
    private val viewModel: ArtworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val argument = intent.getStringExtra("artwork")
        val artwork: DomainArtwork = Gson().fromJson(argument, DomainArtwork::class.java)

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            LaunchedEffect(Unit) {
                // artwork.key가 변경될 때마다 실행됩니다.
                viewModel.getFavoriteArtwork(artworkUid = artwork.key!!)
                viewModel.getLikedArtwork(artworkUid = artwork.key!!)
                viewModel.getLikedCountArtwork(artwork.key!!)
                viewModel.getArtistInfo(artwork.artistUid!!)
                viewModel.getArtistArtworks(artwork.artistUid!!)
            }

            SowoonTheme {
                val artistInfo by viewModel.artistInfo.collectAsState()
                val artworkFavoriteState by viewModel.artworkFavoriteState.observeAsState(initial = false)
                val artworkLikedState by viewModel.artworkLikedState.observeAsState(initial = false)
                val artistArtworks by viewModel.artistArtworks.collectAsState()
                val userInfo by viewModel.userInfo.collectAsState()
                val isLoadingArtistArtworks by viewModel.isLoadingArtistArtworks.collectAsState()
                val artworkLikedCountState by viewModel.artworkLikedCountState.observeAsState(0)
                var requestLogin by remember { mutableStateOf(false) }


                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    ArtworkDetailScreen(
                        artwork = artwork,
                        artistInfo = artistInfo,
                        favoriteState = artworkFavoriteState,
                        likedState = artworkLikedState,
                        userInfo = userInfo,
                        artistArtworks = artistArtworks,
                        isLoadingArtistArtworks = isLoadingArtistArtworks,
                        artworkLikedCount = artworkLikedCountState,
                        likedBtnOnClick = {
                            if (viewModel.userUid != null) {
                                viewModel.setLikedArtwork(artworkLikedState, artwork.key!!)
                            } else {
                                requestLogin = true
                            }
                        },
                        bookmarkBtnOnClick = {
                            if(viewModel.userUid != null){
                                viewModel.setFavoriteArtwork(artworkFavoriteState, artwork.key!!)
                            }else{
                                requestLogin = true
                            }
                        },
                        artistInfoBtnOnClick = {
                            if(!isLoadingArtistArtworks){
                                startActivity(Intent(this, ArtistProfileActivity::class.java)
                                    .putExtra("artist", Gson().toJson(artistInfo, DomainUser::class.java))
                                    .putParcelableArrayListExtra("artistArtworks", ArrayList(artistArtworks))
                                )
                            }
                        },
                        actionBtnOnClick = {
                            if(viewModel.userUid == null){
                                requestLogin = true
                            }else{
                                if(userInfo.uid == artwork.artistUid){
                                    viewModel.confirmArtworkSold(artwork.key!!)
                                }else{
                                    startActivity(Intent(this,ChatRoomActivity::class.java)
                                        .putExtra("artwork", Gson().toJson(artwork, DomainArtwork::class.java))
                                        .putExtra("destUser", Gson().toJson(artistInfo, DomainUser::class.java))
                                    )
                                }
                            }
                        }
                    )

                    if(requestLogin){
                        LoginToastMessage(
                            dismissOnClick = {
                                requestLogin = false
                            },
                            confirmOnClick = {
                                requestLogin = false
                                startActivity(Intent(this, StartActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ArtworkDetailScreen(
    artwork: DomainArtwork,
    artistInfo: DomainUser,
    favoriteState: Boolean,
    likedState: Boolean,
    userInfo: DomainUser,
    artworkLikedCount: Int,
    artistArtworks: List<DomainArtwork>,
    isLoadingArtistArtworks: Boolean,
    likedBtnOnClick: () -> Unit,
    bookmarkBtnOnClick: () -> Unit,
    artistInfoBtnOnClick: () -> Unit,
    actionBtnOnClick: () -> Unit,
) {
    var isZoomDialogOpen by remember { mutableStateOf(false) }
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 100.dp) // 아래에 여유 공간 추가 (버튼 영역 때문)
        ) {
            /** 상단 앱 바 */
            item {
                ArtworkTopBar(
                    modifier = Modifier.background(Color.White),
                    favoriteState,
                ) { bookmarkBtnOnClick() }
            }

            /** 작품 이미지 */
            item {
                AsyncImage(
                    model = artwork.url,
                    contentDescription = "이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9 / 10f)
                        .clickable { isZoomDialogOpen = true },
                    contentScale = ContentScale.Crop
                )
            }

            /** 작품 정보 */
            item {
                artworkInfo(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp),
                    artwork = artwork,
                    likedState = likedState,
                    artworkLikedCount = artworkLikedCount,
                    likedBtnOnClick = { likedBtnOnClick() },
                )
            }

            /** 작가 정보 */
            item {
                artistProfile(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                    artist = artistInfo,
                    artistInfoBtnOnClick = artistInfoBtnOnClick
                )
            }

            /** 작가의 다른 작품들 */
            item {
                artistOtherArtworks(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                    name = artistInfo.name,
                    artistArtworks = artistArtworks,
                    userInfo = userInfo,
                    isLoadingArtistArtworks = isLoadingArtistArtworks
                )
            }

            /** 여유 공간 */
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        /** 하단의 고정된 버튼 */
        userActionButton(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            artwork,
            userInfo = userInfo,
            actionBtnOnClick = actionBtnOnClick
        )

        /** 이미지 확대 Dialog */
        if (isZoomDialogOpen) {
            FullScreenArtwork(
                imageUrls = listOf(artwork.url!!),
                onClose = { isZoomDialogOpen = false }
            )
        }
    }
}



@Composable
fun artistOtherArtworks(
    modifier: Modifier,
    name: String,
    artistArtworks: List<DomainArtwork>,
    userInfo: DomainUser,
    isLoadingArtistArtworks: Boolean
){
    val context = LocalContext.current

    if(isLoadingArtistArtworks){
        Column(modifier = modifier.heightIn(max = 400.dp)) {
            Text(text = "${name}님의 다른 작품들 ", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentHeight(),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalItemSpacing = 5.dp,
            ){
                items(4){ index ->
                    Box(
                        modifier = Modifier.width(150.dp).height(150.dp).clip(RoundedCornerShape(5.dp)).shimmerEffect()
                    ){}
                }
            }
        }
    }else{
        Column(modifier = modifier.heightIn(max = 1000.dp)) {
            Text(text = "${name}님의 다른 작품들 ", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentHeight(),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalItemSpacing = 5.dp,
            ){
                items(artistArtworks.size){ index ->
                    artworkCard(artwork = artistArtworks[index], onClick = {
                        context.startActivity(Intent(context, ArtworkDetailActivity::class.java).apply {
                            putExtra("artwork", Gson().toJson(artistArtworks[index]))
                            putExtra("userInfo", Gson().toJson(userInfo))
                        })
                    })
                }
            }
        }
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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

@Composable
fun artistProfile(modifier: Modifier, artist: DomainUser, artistInfoBtnOnClick: () -> Unit) {
    Column(modifier = modifier) {
        Text(text = "작가", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    artistInfoBtnOnClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .size(60.dp)
                    .background(color = colorResource(id = R.color.lightgray)),
                contentAlignment = Alignment.Center,
            ){
                if(artist.profileImage.isNotEmpty()){
                    Log.d("artistProfile_profileImage", artist.profileImage.toString())
                    AsyncImage(model = artist.profileImage, contentDescription = null, contentScale = ContentScale.Crop)
                } else{
                    Log.d("artistProfile_artist", artist.toString())
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 15.dp)
            ) {
                Text(text = artist!!.name, fontSize = 14.sp, color = Color.Black)
                Text(text = artist.artistProfile.career.graduate, fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(painter = painterResource(id = R.drawable.forward), contentDescription = null)

        }
        Divider(thickness = 0.5.dp, color = Color.LightGray, modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))
    }

}

@Composable
fun userActionButton(
    modifier: Modifier,
    artwork: DomainArtwork,
    userInfo: DomainUser,
    actionBtnOnClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        val price = if(artwork.minimalPrice.isEmpty()) 0 else artwork.minimalPrice.toInt()

        Row(
            modifier = Modifier.padding(20.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AutoResizedText(
                    text = if (artwork.sold) "판매완료" else "판매중 (가격제안 가능)",
                    style = TextStyle(
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = DecimalFormat("#,###").format(price * 10000)+ "원", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Black, overflow = TextOverflow.Visible)
            }
            if(!artwork.sold){
                TextButton(
                    onClick = { actionBtnOnClick() },
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(0.5.dp,color = Color.Black),
                    modifier = Modifier.wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 15.dp)
                ) {
                    if(userInfo.uid == artwork.artistUid){
                        AutoResizedText(
                            text = "판매 확정하기", style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier,
                            color = Color.White
                        )
                    }else{
                        AutoResizedText(
                            text = "메세지로 거래하기", style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun artworkInfo(
    modifier: Modifier,
    artwork: DomainArtwork,
    likedState: Boolean,
    artworkLikedCount: Int,
    likedBtnOnClick: () -> Unit,
){
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().wrapContentSize()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artwork.name!!,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    overflow = TextOverflow.Visible
                )
                Text(
                    text = artwork.madeIn.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }



            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Icon(
                    if(!likedState) Icons.Filled.FavoriteBorder else Icons.Filled.Favorite,
                    contentDescription = "좋아요",
                    tint = if(likedState) Color.Red else Color.Black,
                    modifier = Modifier.size(30.dp).noRippleClickable {
                        likedBtnOnClick()
                    }
                )
                Text(artworkLikedCount.toString(), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Divider(thickness = 0.5.dp, color = Color.LightGray, modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))

        Text(text = artwork.review!!, color = Color.Black, fontSize = 14.sp)

        Spacer(modifier = Modifier.padding(top = 30.dp))


        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "카테고리", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Text(text = artwork.category!!, color = Color.Black, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "사이즈", color = Color.Gray, fontSize = 14.sp)
                Text(text = artwork.size!!, color = Color.Black, fontSize = 14.sp)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "재료", color = Color.Gray, fontSize = 14.sp)
                Text(text = artwork.material!!, color = Color.Black, fontSize = 14.sp)
            }
        }

        Divider(thickness = 0.5.dp, color = Color.LightGray, modifier = Modifier.padding(top = 15.dp, bottom = 15.dp))
    }
}

@Composable
fun artworkLikeBtn(likedState: Boolean, likedBtnOnClick: () -> Unit, ){

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

@Preview(showSystemUi = true)
@Composable
fun artworkActivityTest(){
    val artwork = DomainArtwork(
        name = "백작",
        madeIn = "2024",
        review = "this is made in 2024",
        category = "한국화",
        size = "20 * 30",
        material = "acril",
        minimalPrice = "15"
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        ArtworkDetailScreen(
            artwork = DomainArtwork(artistUid = "123", name = "하울의 움직이는 섬하울의 움직이는 섬dlasdf", madeIn = "2025"),
            artistInfo = DomainUser(name = "정은숙"),
            favoriteState = true,
            likedState = true,
            userInfo = DomainUser(),
            artworkLikedCount = 1,
            artistArtworks = listOf(artwork, artwork,artwork),
            isLoadingArtistArtworks = true,
            likedBtnOnClick = {},
            bookmarkBtnOnClick = {},
            artistInfoBtnOnClick = {}
        ) { }
    }
}