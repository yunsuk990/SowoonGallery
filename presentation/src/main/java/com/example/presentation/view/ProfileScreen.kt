package com.example.presentation.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
        ProfileTopBar() { newSort -> sortedBy = newSort }
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
                            artworkCard(artwork = artworkList!![it], viewModel){
                                context.startActivity(Intent(context, ArtworkActivity::class.java).apply {
                                    putExtra("artwork", Gson().toJson(artworkList!![it]))
                                })
                            }
                        }
                    }
                }
            },
            contentPadding = PaddingValues(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalItemSpacing = 5.dp,
        )
    }
}
@Composable
fun artworkCard(artwork: DomainArtwork, viewModel: MainViewModel, onClick: () -> Unit){
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
    }
}
@Composable
fun LikeBtn(
    likedState: Boolean,
    modifier: Modifier,
    likedBtnOnClick: () -> Unit,
){

    Box(
        modifier = Modifier
            .wrapContentSize() // IconButton 크기
            .clickable(
                onClick = { likedBtnOnClick() },
                indication = null, // Ripple 효과 제거
                interactionSource = remember { MutableInteractionSource() } // 사용자 상호작용 추적
            )
            .padding(end = 3.dp),
        contentAlignment = Alignment.Center // 아이콘 정렬
    ){
        if (!likedState) {
            androidx.compose.material.Icon(
                painterResource(id = R.drawable.heart_border),
                contentDescription = "좋아요",
                modifier = modifier
            )
        } else {
            androidx.compose.material.Icon(
                painterResource(id = R.drawable.heart_filled),
                contentDescription = "좋아요",
                modifier = modifier,
                tint = Color.Red

            )
        }
    }
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
fun ProfileTopBar(
    onSortByChanged: (ArtworkSort) -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun profileScreenTest(){
    Surface(modifier = Modifier.fillMaxSize()){
        Column {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "작품",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}, enabled = false) {}
                },
                actions = {
                          Icon(painterResource(id = R.drawable.home_border), contentDescription = null, tint = Color.Black)
                },
            )
            Divider(thickness = 0.5.dp, color = Color.LightGray)
        }
    }
}