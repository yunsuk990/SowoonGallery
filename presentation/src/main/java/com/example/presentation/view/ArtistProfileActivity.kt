package com.example.presentation.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import com.example.domain.model.Career
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.ArtworkSort
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ArtworkViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.ranges.coerceIn

@AndroidEntryPoint
class ArtistProfileActivity : ComponentActivity() {

    private val viewModel: ArtworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var artistIntent = intent.getStringExtra("artist")
        val artistInfo = Gson().fromJson(artistIntent, DomainUser::class.java)

        var artistArtwork: List<DomainArtwork> = intent.getParcelableArrayListExtra("artistArtworks")!!

        var bestArtwork = artistArtwork.maxBy { it.likedArtworks.size }


        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            LaunchedEffect(artistArtwork) {
                viewModel.setData(artistArtwork)
            }


            val userInfo by viewModel.userInfo.collectAsState()
            val artistArtworks by viewModel.artistArtworks.collectAsState()

            Log.d("artistArtworks", artistArtworks.toString())

            SowoonTheme {
                ArtistProfileScreen(
                    artistInfo = artistInfo,
                    userInfo = userInfo,
                    artistArtwork = artistArtworks,
                    bestArtwork = bestArtwork,
                    artworkFilterChange = { artworkSort ->
                        viewModel.filterArtworks(artworkSort, artistArtwork)
                    },
                    artistIntroduce = { artistIntroduce ->
                        Log.d("artistIntroduce", artistIntroduce)
                        viewModel.updateArtistProfile(artistIntroduce)
                    },
                    artistCareerOnChange = { career ->
                        viewModel.updateArtistProfile(career)
                    }
                )
            }
        }
    }
}

@Composable
fun ArtistProfileScreen(
    artistInfo: DomainUser,
    artistArtwork: List<DomainArtwork>,
    bestArtwork: DomainArtwork,
    userInfo: DomainUser,
    artworkFilterChange: (ArtworkSort) -> Unit,
    artistIntroduce: (String) -> Unit,
    artistCareerOnChange: (Career) -> Unit
) {
    var scrollOffset by remember { mutableStateOf(0f) } // 스크롤 offset
    var lazyListState = rememberLazyStaggeredGridState()

    val nestedConnection = remember {
        object: NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val isAtBottom = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1

                if (isAtBottom && available.y > 0) {
                    scrollOffset = (scrollOffset - available.y).coerceIn(0f, 500f)
                }

                return super.onPostScroll(consumed, available, source)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = (scrollOffset - available.y).coerceIn(0f, 500f)
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .nestedScroll(nestedConnection)
    ) {
        ArtistProfileTopBar()
        ArtistInfo(bestArtwork = bestArtwork, scrollOffset = scrollOffset, artistInfo = artistInfo, userInfo = userInfo, artistIntroduceUpdate = artistIntroduce)
        ArtistProfileMenu(
            artistInfo = artistInfo,
            userInfo = userInfo,
            artistArtwork = artistArtwork,
            lazyListState = lazyListState,
            artistCareerOnChange = artistCareerOnChange,
            artworkFilterChange = artworkFilterChange
        )
    }
}


@Composable
fun ArtistInfo(bestArtwork: DomainArtwork, scrollOffset: Float = 0f, artistInfo: DomainUser, userInfo: DomainUser, artistIntroduceUpdate: (String) -> Unit) {
    val year = SimpleDateFormat("yyyy").format(Date()).toInt()
    val birth = artistInfo.birth.split("/").first().toInt()
    var artistIntroduce by remember { mutableStateOf(artistInfo.artistProfile.introduce) }
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((480f - scrollOffset).coerceAtLeast(0f).dp)
    ){
        Column {
            AsyncImage(
                model = bestArtwork.url,
                contentDescription = "Artist Recent Artwork",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // 이미지 높이도 Box 높이에 맞춰서 동기화
                    .background(color = colorResource(R.color.lightgray)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp)
            ){
                Row() {
                    Column {
                        Text(text = artistInfo.name, fontFamily = FontFamily.Serif, fontSize = 22.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${birth}, ${year-birth}세", color = Color.Gray, modifier = Modifier.padding(top=3.dp), fontFamily = FontFamily.Serif, fontSize = 16.sp, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if(userInfo.mode == 1 ){
                        Button(
                            onClick = {
                                if(isEditing){
                                    artistIntroduceUpdate(artistIntroduce)
                                    isEditing = false
                                }else{
                                    isEditing = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) { Text(text = if(isEditing) "저장하기" else "수정하기") }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                EditableArtistIntroduce(
                    text = artistIntroduce,
                    onValueChange = { artistIntroduce = it},
                    isEditing = isEditing,
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = "프로필 소개글"
                )
            }
        }

    }
}

@Composable
fun ArtistProfileMenu(
    artistInfo: DomainUser,
    userInfo: DomainUser,
    artistCareerOnChange: (Career) -> Unit,
    artistArtwork: List<DomainArtwork>,
    lazyListState: LazyStaggeredGridState,
    artworkFilterChange: (ArtworkSort) -> Unit
){
    val tabTitles = listOf("소개", "작품", "판매 작품")
    val pagerState = rememberPagerState(pageCount = {tabTitles.size}, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    Column() {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
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
                0 -> artistIntroduceScreen(artistInfo, userInfo = userInfo, artistCareerOnChange = artistCareerOnChange)
                1 -> artistArtworksScreen(artworks = artistArtwork, lazyListState = lazyListState, artworkFilterChange = artworkFilterChange)
                2 -> artistSoldArtworksScreen()
            }
        }
    }
}

@Composable
fun EditableArtistIntroduce(
    text: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    modifier: Modifier,
    placeHolder: String
) {
    var value by remember { mutableStateOf(text) }
    if (isEditing) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                value = it
                onValueChange(it)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Black,
                cursorColor = Color.Black
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            ),
            placeholder = { Text(text = placeHolder) },
            modifier = modifier,
            maxLines = 4,
            singleLine = false
        )
    } else {
        Text(
            text = value,
            modifier = modifier,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 4,
        )
    }
}

@Composable
fun artistIntroduceScreen(artistInfo: DomainUser, userInfo: DomainUser, artistCareerOnChange: (Career) -> Unit) {
    var artistProfile = artistInfo.artistProfile.career
    var isEditing by remember { mutableStateOf(false) }

    var graudate by remember { mutableStateOf(artistProfile.graduate) }
    var awards by remember { mutableStateOf(artistProfile.awards)}
    var exhibition by remember { mutableStateOf(artistProfile.exhibition) }

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
                       modifier = Modifier
                           .wrapContentWidth()
                           .height(200.dp)
                           .background(colorResource(R.color.lightwhite))
                   )
               }
           }
           Spacer(modifier = Modifier.height(15.dp))
           Column(
               modifier = Modifier.fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               Text(artistInfo.name, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
               EditableArtistIntroduce(
                   text  = graudate,
                   onValueChange = { newText -> graudate = newText },
                   isEditing = isEditing,
                   modifier = Modifier.padding(top = 8.dp),
                   placeHolder = "최종학력"
               )
           }
           Text(text = "수상경력", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 25.dp))

           EditableArtistIntroduce(
               text  = awards,
               onValueChange = { newText -> awards = newText },
               isEditing = isEditing,
               modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
               placeHolder = "수상경력"
           )

           Text(text = "주요 전시", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 25.dp))

           EditableArtistIntroduce(
               text = exhibition,
               onValueChange = { newText -> exhibition = newText },
               isEditing = isEditing,
               modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
               placeHolder = "주요 전시"
           )

           if(userInfo.mode == 1){
               Spacer(modifier = Modifier.height(20.dp))
               Button(
                   onClick = {
                       if(isEditing){
                           artistCareerOnChange(
                               Career(
                                   graduate = graudate,
                                   awards = awards,
                                   exhibition = exhibition
                               )
                           )
                           isEditing = false
                       }else{
                           isEditing = true
                       }
                   },
                   colors = ButtonDefaults.buttonColors(
                       containerColor = Color.Black
                   ),
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 15.dp)
               ) { Text(text = if(isEditing) "저장하기" else "수정하기") }
           }
       }
   }
}

@Composable
fun artistArtworksScreen(artworks: List<DomainArtwork>, lazyListState: LazyStaggeredGridState, artworkFilterChange: (ArtworkSort) -> Unit){
    LaunchedEffect(Unit) {
        artworkFilterChange(ArtworkSort.NONE)
    }

    Column() {
        artworkDropDownMenu(artworks = artworks, artworkFilterChange = artworkFilterChange)
        artistArtworksGridLayout(artworks = artworks, lazyListState)
    }
}



@Composable
fun artistArtworksGridLayout(artworks: List<DomainArtwork>, lazyListState: LazyStaggeredGridState){
    val context = LocalContext.current
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .padding(top = 20.dp, start = 15.dp, end = 15.dp)
            .fillMaxHeight(1f),
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

        Column(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Text(artwork.name!!, color = Color.Black, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if(artwork.sold){
                Text("판매완료", color = Color.Gray, fontSize = 12.sp)
            }else{
                Text( text = DecimalFormat("#,###").format(artwork.minimalPrice.toInt() * 10000)+ "원", color = Color.Gray, fontSize = 12.sp, lineHeight = 10.sp)
            }
        }
    }
}


@Composable
fun artworkDropDownMenu(artworks: List<DomainArtwork>, artworkFilterChange: (ArtworkSort) -> Unit){
    var title by remember{ mutableStateOf("") }
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${artworks.size} 개 작품 :", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = title, fontSize = 14.sp)
        DropDownMenu(
            dropDownMenuExpanded = dropDownMenuExpanded,
            onDismissRequest = {
                title = ""
                artworkFilterChange(ArtworkSort.NONE)
                dropDownMenuExpanded = false},
            onClick = {
                title = it.sort
                artworkFilterChange(it)
                dropDownMenuExpanded = false
            }
        )
        IconButton(onClick = { dropDownMenuExpanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.filter_list),
                contentDescription = "정렬기준",
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        dropDownMenuExpanded = true
                    }
            )
        }
    }
    Divider(thickness = 1.dp, color = Color.LightGray)
}

@Composable
fun DropDownMenu(dropDownMenuExpanded: Boolean, onDismissRequest: () -> Unit, onClick: (ArtworkSort) -> Unit){
    var list = mutableListOf(ArtworkSort.LIKE, ArtworkSort.BOOKMARK, ArtworkSort.DATE)
    Box(){
        DropdownMenu(
            expanded = dropDownMenuExpanded,
            onDismissRequest = { onDismissRequest() },
            modifier = Modifier.background(Color.White)
        ) {
            list.forEachIndexed { index, artworkSort ->
                DropdownMenuItem(
                    text = { Text(artworkSort.sort) },
                    onClick = { onClick(artworkSort) }
                )
            }
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
        artistInfo = DomainUser(name = "정은숙", profileImage = "1234", birth = "1965/01/05"),
        artistArtwork = listOf(
            DomainArtwork(name = "SWAIN", sold = false, minimalPrice = "15"),
            DomainArtwork(name = "COLD", sold = false, minimalPrice = "10")),
        bestArtwork = DomainArtwork(),
        userInfo = DomainUser(mode = 1),
        artistIntroduce = { value -> },
        artistCareerOnChange = {value ->},
        artworkFilterChange = { value ->}
    )
}