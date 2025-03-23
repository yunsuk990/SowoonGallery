package com.yschoi.presentation.view

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.presentation.R
import com.yschoi.presentation.model.ArtworkSort
import com.yschoi.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun ArtworkScreen(viewModel: MainViewModel) {
    //val refreshing by viewModel.isRefreshing.collectAsState()

    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val artworkList by viewModel.artworkLiveData.collectAsState()
    var sortedBy by remember { mutableStateOf(ArtworkSort.NONE) }
    val context = LocalContext.current
    val list = arrayListOf("전체", "한국화", "수채화", "아크릴화", "도자기")

    LaunchedEffect(sortedBy) {
        viewModel.sortArtworks(sortedBy, category = list[selectedIndex])
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileTopBar() { newSort -> sortedBy = newSort }
            artworkCategoryRow(list,selectedIndex) { index, title ->
                selectedIndex = index
                viewModel.sortCategoryArtworks(title)
            }
            artworkGridLayout(
                artworkList = artworkList,
                artworkOnClick = { artwork ->
                    viewModel.saveRecentCategory(artwork.category!!)
                    var intent = Intent(context, ArtworkDetailActivity::class.java)
                    intent.putExtra("artworkId", artwork.key)
                    intent.putExtra("artistUid", artwork.artistUid)
                    context.startActivity(intent)
                }
            )
        }
        if(userInfo.mode == 1){
            addArtworkForArtists(Modifier.align(Alignment.BottomEnd), onClick = {
                context.startActivity(Intent(context, ArtworkUploadActivity::class.java))
            })
        }
    }
}

@Composable
fun artworkCategoryRow(list: List<String>, selectedIndex: Int, onClick: (Int, String) -> Unit){
    LazyRow(
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            itemsIndexed(list){ index, title ->
                categoryButton(buttonText = title, isSelected = index == selectedIndex) {
                    onClick(index, title)
                }
            }
        }
    )
}

@Composable
fun artworkGridLayout(artworkList: List<DomainArtwork>, artworkOnClick: (DomainArtwork) -> Unit){
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        content = {
            artworkList?.let { list ->
                if(list.isNotEmpty()){
                    items(artworkList!!.size) {
                        artworkCard(artwork = artworkList[it]){ artworkOnClick(artworkList[it]) }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalItemSpacing = 5.dp,
    )
}
@Composable
fun artworkCard(artwork: DomainArtwork, onClick: () -> Unit) {
    AsyncImage(
        model = artwork.url,
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .clip(RoundedCornerShape(5.dp))
            .clickable { onClick() },
        contentDescription = "이미지"
    )
}

@Composable
fun categoryButton(buttonText: String, isSelected: Boolean, onClick: () -> Unit){

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
        colors = if(isSelected) ButtonDefaults.outlinedButtonColors(Color.Black) else ButtonDefaults.outlinedButtonColors(
            colorResource(id = R.color.white)),
        border = if(isSelected) null else BorderStroke(1.dp, color = colorResource(id = R.color.lightwhite))
    ) {
        Text(text = buttonText, color = if(isSelected) Color.White else Color.Black, fontSize = 14.sp, modifier = Modifier.padding(0.dp))
    }
}

//AppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(onSortByChanged: (ArtworkSort) -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {},
        actions = {
                  FilterDropDownMenu(onSortByChanged)
                  //ExposedDropDownMenu(onSortByChanged)
        },
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun FilterDropDownMenu(onSortByChanged: (ArtworkSort) -> Unit){
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var list = listOf(ArtworkSort.DATE, ArtworkSort.LIKE, ArtworkSort.BOOKMARK, ArtworkSort.PRICE)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 12.sp)
        IconButton(onClick = { dropDownMenuExpanded = true }) {
            Icon(painter = painterResource(id = R.drawable.filter_list), contentDescription = "정렬기준")
        }
    }
    DropdownMenu(
        expanded = dropDownMenuExpanded,
        onDismissRequest = {
            onSortByChanged(ArtworkSort.NONE)
            title = ""
            dropDownMenuExpanded = false
        },
        containerColor = Color.White,
        modifier = Modifier.wrapContentSize()
    ) {

        list.forEachIndexed { index, artworkSort ->
            DropdownMenuItem(
                text = { Text(text = artworkSort.sort)},
                onClick = {
                    title = artworkSort.sort
                    onSortByChanged(artworkSort)
                    dropDownMenuExpanded = false
                }
                //leadingIcon = { Icon(imageVector =  Icons.Filled.Favorite, contentDescription = "좋아요") },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun profileScreenTest(){
    val artworkList = arrayListOf<DomainArtwork>()
    val list = arrayListOf("전체", "한국화", "수채화", "아크릴화", "도자기")
    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize(),
            ) {
                ProfileTopBar() {}
                artworkCategoryRow(list,0) { index, title -> }
                artworkGridLayout(artworkList, artworkOnClick = {})
            }
            addArtworkForArtists(Modifier.align(Alignment.BottomEnd), onClick = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier.pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}

@Composable
fun addArtworkForArtists(modifier: Modifier, onClick: () -> Unit){
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(50),
        containerColor = Color.Black,
        contentColor = Color.White,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
    }
}