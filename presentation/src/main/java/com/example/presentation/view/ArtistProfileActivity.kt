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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.ranges.coerceIn

class ArtistProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var artistIntent = intent.getStringExtra("artist")
        val artistInfo = Gson().fromJson(artistIntent, DomainUser::class.java)

        var artistArtwork: List<DomainArtwork> = intent.getParcelableArrayListExtra("artistArtworks")!!

        Log.d("ArtistProfilActivity", "artistArtwork: ${artistArtwork}")

        var bestArtwork = artistArtwork.maxBy { it.likedArtworks.size }
        Log.d("ArtistProfileActivity", "BestArtwork: ${bestArtwork}")

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

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
    var lazyListState = rememberLazyStaggeredGridState()

    val nestedConnection = remember {
        object: NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val isAtBottom = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1

                if (isAtBottom && available.y > 0) {
                    scrollOffset = (scrollOffset - available.y).coerceIn(0f, 410f)
                }

                return super.onPostScroll(consumed, available, source)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = (scrollOffset - available.y).coerceIn(0f, 410f)
                val usedScrollY = scrollOffset - newOffset

                scrollOffset = newOffset

                // 내부 리스트가 최상단이면 부모(Column)가 스크롤됨
                if (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0) {
                    return Offset(0f, usedScrollY)
                }

                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).nestedScroll(nestedConnection)
    ) {
        ArtistProfileTopBar()
        ArtistInfo(bestArtwork = bestArtwork, scrollOffset = scrollOffset, artistInfo = artistInfo)
        ArtistProfileMenu(
            artistInfo = artistInfo,
            artistArtwork = artistArtwork,
            lazyListState = lazyListState
        )
    }
}


@Composable
fun ArtistInfo(bestArtwork: DomainArtwork, scrollOffset: Float = 0f, artistInfo: DomainUser) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((410f - scrollOffset).coerceAtLeast(0f).dp)
    ){
        Column {
            AsyncImage(
                model = bestArtwork.url,
                contentDescription = "Artist Recent Artwork",
                modifier = Modifier.fillMaxWidth().height(300.dp)  // 이미지 높이도 Box 높이에 맞춰서 동기화
                    .background(color = colorResource(R.color.lightgray)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp)
            ){
                Text(text = artistInfo.name, fontFamily = FontFamily.Serif, fontSize = 22.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "1965, ${artistInfo.age}세", color = Color.Gray, modifier = Modifier.padding(top=3.dp), fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            }
        }

    }
}

@Composable
fun ArtistProfileMenu(
    artistInfo: DomainUser,
    artistArtwork: List<DomainArtwork>,
    lazyListState: LazyStaggeredGridState
){
    val tabTitles = listOf("소개", "작품", "판매 작품")
    val pagerState = rememberPagerState(pageCount = {tabTitles.size}, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    Column() {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        currentTabPosition = tabPositions.get(pagerState.currentPage)
                    ),
                    color = Color.Black,
                    height = 1.5.dp
                )
            },
            divider = {}
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
                    },
                    selectedContentColor = Color.White
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
                1 -> artistArtworksScreen(artworks = artistArtwork, lazyListState)
                2 -> artistSoldArtworksScreen()
            }
        }
    }
}

@Composable
fun artistIntroduceScreen(artistInfo: DomainUser) {
   LazyColumn(
       modifier = Modifier.padding(15.dp)
   ) {
       item {
           if(artistInfo.profileImage.isNotEmpty()) {
               Column(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   AsyncImage(
                       model = artistInfo.profileImage,
                       contentDescription = null,
                       modifier = Modifier.wrapContentWidth().height(200.dp).background(colorResource(R.color.lightwhite))
                   )
               }
           }
           Spacer(modifier = Modifier.height(15.dp))
           Text(artistInfo.name, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
           Text(text = "홍익대학교 미술대학 졸업(1987년)", fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), textAlign = TextAlign.Center)

           Text(text = "수상경력", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 25.dp))
           Text(text = "대한민국미술대전, 경기미술대전, 남농 미술대전, 목우회 등\n\n소울갤러리 대표\n" +
                   "한국미협, 동양수묵연구원, 파인아트클럽, 아태미협회원", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))

           Text(text = "주요 전시", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 25.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
           Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
       }
   }
}

@Composable
fun artistArtworksScreen(artworks: List<DomainArtwork>, lazyListState: LazyStaggeredGridState){
    Column() {
        artworkDropDownMenu(artworks = artworks)
        artistArtworksGridLayout(artworks = artworks, lazyListState)
    }
}



@Composable
fun artistArtworksGridLayout(artworks: List<DomainArtwork>, lazyListState: LazyStaggeredGridState){
    val context = LocalContext.current
    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp).fillMaxHeight(1f),
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalItemSpacing = 15.dp,
        state = lazyListState,
    ) {
        items(artworks.size){ index ->
            artistArtworkCard(
                artwork = artworks[index],
                onClick = {
                    context.startActivity(Intent(context, ArtworkDetailActivity::class.java).putExtra("artwork", Gson().toJson(artworks[index])))
                }
            )
        }
    }
}

@Composable
fun artistArtworkCard(artwork: DomainArtwork, onClick: () -> Unit){
    Column(
        modifier = Modifier.noRippleClickable { onClick() },
    ) {
        AsyncImage(
            model = artwork.url,
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .clip(RoundedCornerShape(5.dp)),
            contentDescription = "이미지"
        )
//        Image(
//            painter = painterResource(R.drawable.artist_profile),
//            modifier = Modifier
//                .wrapContentHeight()
//                .wrapContentWidth()
//                .clip(RoundedCornerShape(5.dp))
//                .clickable { onClick() },
//            contentDescription = "이미지"
//        )
        Column(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Text(artwork.name!!, color = Color.Black, fontSize = 14.sp)
            if(artwork.sold){
                Text("판매완료", color = Color.Gray, fontSize = 12.sp)
            }else{
                Text( text = DecimalFormat("#,###").format(artwork.currentPrice * 10000)+ "원", color = Color.Gray, fontSize = 12.sp)
            }
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
        artistInfo = DomainUser(name = "정은숙", profileImage = "1234"),
        artistArtwork = listOf(
            DomainArtwork(name = "SWAIN", sold = false, currentPrice = 15),
            DomainArtwork(name = "COLD", sold = false, currentPrice = 10)),
        bestArtwork = DomainArtwork()
    )
}