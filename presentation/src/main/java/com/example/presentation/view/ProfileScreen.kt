package com.example.presentation.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.model.ArtworkSort
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var list = arrayListOf("전체", "한국화", "수채화", "아크릴화")
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val artworkList by viewModel.artworkLiveData.collectAsState()
    var sortedBy by remember { mutableStateOf(ArtworkSort.NONE) }
    
    LaunchedEffect(sortedBy){
        viewModel.sortArtworks(sortedBy,list[selectedIndex])
    }

    Log.d("artworkList", artworkList?.size.toString())
    var context = LocalContext.current

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
        ProfileTopBar(scrollBehavior,) { newSort -> sortedBy = newSort }
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                itemsIndexed(list){ index, title ->
                    categoryButton(buttonText = title, isSelected = index == selectedIndex) {
                        //firebase 데이터 가져오기
                        viewModel.loadArtworks(title)
                        selectedIndex = index
                    }
                }
            }
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            content = {
                artworkList?.let { list ->
                    if(list.isNotEmpty()){
                        items(artworkList!!.size) {
                            artworkCard(artwork = artworkList!![it]){
                                context.startActivity(Intent(context, ArtworkActivity::class.java).apply {
                                    putExtra("artwork", Gson().toJson(artworkList!![it]))
                                })
                            }
                        }
                    }
                }
            },
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp,
        )
    }
}
@Composable
fun artworkCard(artwork: DomainArtwork, onClick: () -> Unit){
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
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
        colors = if(isSelected) ButtonDefaults.outlinedButtonColors(colorResource(id = R.color.lightgray)) else ButtonDefaults.outlinedButtonColors(Color.White)
    ) {
        Text(text = buttonText, color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(0.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onSortByChanged: (ArtworkSort) -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
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
        scrollBehavior = scrollBehavior,
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun FilterDropDownMenu(
    onSortByChanged: (ArtworkSort) -> Unit
){
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
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
        modifier = Modifier.wrapContentSize()
    ) {
        DropdownMenuItem(
            text = { Text(text = "좋아요 순")},
            onClick = {
                title = "좋아요 순"
                onSortByChanged(ArtworkSort.LIKE)
                dropDownMenuExpanded = false
            },
            leadingIcon = { Icon(imageVector =  Icons.Filled.Favorite, contentDescription = "좋아요") },
        )
        DropdownMenuItem(
            text = { Text(text = "북마크 순")},
            onClick = {
                title = "북마크 순"
                onSortByChanged(ArtworkSort.BOOKMARK)
                dropDownMenuExpanded = false
            },
            leadingIcon = { Icon(painterResource(id = R.drawable.bookmark_filled), contentDescription = "북마크") },
        )
        DropdownMenuItem(
            text = { Text(text = "날짜 순")}, 
            onClick = {
                title = "날짜 순"
                onSortByChanged(ArtworkSort.DATE)
                dropDownMenuExpanded = false
            },
            leadingIcon = { Icon(imageVector =  Icons.Filled.DateRange, contentDescription = "날짜") },
        )
    }
}