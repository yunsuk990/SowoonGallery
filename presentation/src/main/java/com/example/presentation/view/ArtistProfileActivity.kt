package com.example.presentation.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.ranges.coerceIn

class ArtistProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var artistIntent = intent.getStringExtra("artist")
        val artistInfo = Gson().fromJson(artistIntent, DomainUser::class.java)

        var artistArtwork: List<DomainArtwork> = intent.getParcelableArrayListExtra("artistArtworks")!!

        var bestArtwork = artistArtwork.maxBy { it.likedArtworks.size }
        Log.d("ArtistProfileActivity", "BestArtwork: ${bestArtwork}")

        setContent {

            Log.d("ArtistProfileActivity", "artistInfo: {$artistInfo}")
            Log.d("ArtistProfileActivity", "artistArtworks: $artistArtwork")

            SowoonTheme {
                ArtistProfileScreen(
                    artistInfo = artistInfo,
                    artistArtwork = artistArtwork,
                    bestArtwork = bestArtwork,
                )
            }
        }
    }
}

@Composable
fun ArtistProfileScreen(artistInfo: DomainUser, artistArtwork: List<DomainArtwork>, bestArtwork: DomainArtwork) {
    var scrollOffset by remember { mutableStateOf(0f) } // 스크롤 offset

    val nestedScrollConnection = remember {
        object: NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // scrollOffset을 더 크게 변화시킴으로써 이미지 축소를 더 많이 할 수 있습니다
                val newOffset = scrollOffset - available.y
                // scrollOffset이 지나치게 커지지 않도록 제한 (예: 최대 400f)
                scrollOffset = newOffset.coerceIn(0f, 300f)
                Log.d("NestedScrollConnection", "newOffset: ${newOffset}, scrollOffset: ${scrollOffset}, available: {${available.y}")
                return super.onPreScroll(available, source)
            }

        }
    }

    
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).nestedScroll(nestedScrollConnection)
    ) {
        ArtistProfileTopBar()
        LazyColumn {
            item {
                ArtistInfo(bestArtwork = bestArtwork, scrollOffset = scrollOffset)
            }
            item {
                Text(text = artistInfo.name, fontSize = 22.sp, modifier = Modifier.padding(20.dp), letterSpacing = 1.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            item {
                ArtistProfileMenu(
                    artistInfo = artistInfo,
                    artistArtwork = artistArtwork
                )
            }
        }
    }
}


@Composable
fun ArtistInfo(bestArtwork: DomainArtwork, scrollOffset: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((300f - scrollOffset).coerceAtLeast(0f).dp)
    ){
        Column {
            AsyncImage(
                model = bestArtwork.url,
                contentDescription = "Artist Recent Artwork",
                modifier = Modifier.fillMaxWidth().height((300f - scrollOffset).coerceAtLeast(0f).dp)  // 이미지 높이도 Box 높이에 맞춰서 동기화
                    .background(color = colorResource(R.color.lightgray)),
                contentScale = ContentScale.Crop
            )
        }

    }
}

@Composable
fun ArtistProfileMenu(
    artistInfo: DomainUser,
    artistArtwork: List<DomainArtwork>
){
    val tabTitles = listOf("소개", "작품", "판매 작품")
    val pagerState = rememberPagerState(pageCount = {tabTitles.size})
    val coroutineScope = rememberCoroutineScope()
    Column() {
        androidx.compose.material.TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color.Black,
                    height = 1.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            color = if (pagerState.currentPage == index) {
                                Color.Black
                            } else {
                                Color.LightGray
                            }
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            pageSize = PageSize.Fill,
            state = pagerState,
            userScrollEnabled = true
        ) { page ->
            when(page){
                0 -> artistIntroduceScreen(artistInfo)
                1 -> artistArtworksScreen(artworks = artistArtwork)
                2 -> artistSoldArtworksScreen()
            }
        }
    }
}

@Composable
fun artistIntroduceScreen(artistInfo: DomainUser) {
   Box(){
       Text(
           text = artistInfo.review,
           fontSize = 16.sp,
           color = Color.Black,
           modifier = Modifier.padding(20.dp)
       )
   }
}

@Composable
fun artistArtworksScreen(artworks: List<DomainArtwork>){
    Column() {
        artworkDropDownMenu(artworks = artworks)
        artistArtworksGridLayout(artworks = artworks)
    }
}



@Composable
fun artistArtworksGridLayout(artworks: List<DomainArtwork>){
    val context = LocalContext.current
    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp).heightIn(max = 500.dp),
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalItemSpacing = 15.dp,
    ) {
        items(artworks.size){ index ->
            artistArtworkCard(
                artwork = artworks[index],
                onClick = {
                    context.startActivity(Intent(context, ArtworkDetailActivity::class.java)
                        .putExtra("artwork", Gson().toJson(artworks[index]))
                    )
                }
            )
        }
//        items(20){
//            artistArtworkCard(
//                artwork = DomainArtwork(),
//                onClick = {}
//            )
//        }
    }
}

@Composable
fun artistArtworkCard(artwork: DomainArtwork, onClick: () -> Unit){
    Column {
        AsyncImage(
            model = artwork.url,
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .clip(RoundedCornerShape(5.dp))
                .clickable { onClick() },
            contentDescription = "이미지"
        )
//        Image(painter = painterResource(R.drawable.artist_profile),
//            modifier = Modifier
//                .wrapContentHeight()
//                .wrapContentWidth()
//                .clip(RoundedCornerShape(5.dp))
//                .clickable { onClick() },
//            contentDescription = "이미지")
        Column(
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Text(artwork.name!!, color = Color.Black, fontSize = 14.sp)
            Text(artwork.sold.toString(), color = Color.Gray, fontSize = 12.sp)
        }
    }
}


@Composable
fun artworkDropDownMenu(artworks: List<DomainArtwork>){
    var title by remember{ mutableStateOf("") }
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${artworks.size} 개 작품 :", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = title, fontSize = 14.sp)
        DropDownMenu(
            dropDownMenuExpanded = dropDownMenuExpanded,
            onDismissRequest = {
                title = ""
                dropDownMenuExpanded = false},
            onClick = {
                title = it
                dropDownMenuExpanded = false
            }
        )
        IconButton(onClick = { dropDownMenuExpanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.filter_list),
                contentDescription = "정렬기준",
                modifier = Modifier.size(22.dp).noRippleClickable {
                    dropDownMenuExpanded = true
                }
            )
        }
    }
    Divider(thickness = 1.dp, color = Color.LightGray)
}

@Composable
fun DropDownMenu(dropDownMenuExpanded: Boolean, onDismissRequest: () -> Unit, onClick: (String) -> Unit){
    Box(){
        DropdownMenu(
            expanded = dropDownMenuExpanded,
            onDismissRequest = { onDismissRequest() },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("좋아요 순") },
                onClick = { onClick("좋아요 순") }
            )
            DropdownMenuItem(
                text = { Text("북마크 순") },
                onClick = { onClick("북마크 순") }
            )
            DropdownMenuItem(
                text = { Text("날짜 순") },
                onClick = { onClick("날짜 순") }
            )
        }
    }
}

@Composable
fun artistSoldArtworksScreen(){}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistProfileTopBar(){
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
        actions = {}
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ArtistProfileScreen(
        artistInfo = DomainUser(),
        artistArtwork = listOf(DomainArtwork()),
        bestArtwork = DomainArtwork()
    )
}