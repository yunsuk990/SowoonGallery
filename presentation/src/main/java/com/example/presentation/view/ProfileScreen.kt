package com.example.presentation.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.R
import com.example.presentation.model.Artwork
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun ProfileScreen(){
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var list = arrayListOf("Home", "Work", "Travel", "Fitness", "Books", "Music", "Games")
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var artworks = testItem()

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
        ProfileTopBar(scrollBehavior = scrollBehavior)
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                itemsIndexed(list){ index, title ->
                    categoryButton(buttonText = title, isSelected = index == selectedIndex) { selectedIndex = index }
                }
            }
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            content = {
                items(artworks.size) {
                    artworkCard(artwork = artworks.get(it))
                }
            },
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun artworkCard(artwork: Artwork){
    val context = LocalContext.current
    Card(
        modifier = Modifier.wrapContentHeight().wrapContentWidth(),
        onClick = {
            context.startActivity(Intent(context, ArtworkActivity::class.java).apply {
                putExtra("artwork", artwork)
            })
        }
        ){
        Image(
            painter = painterResource(id = artwork.image),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.clip(RoundedCornerShape(15.dp))
        )
    }
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
fun ProfileTopBar(scrollBehavior: TopAppBarScrollBehavior){
    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {},
        actions = {},
        scrollBehavior = scrollBehavior
    )
}

fun testItem(): ArrayList<Artwork> {
    return arrayListOf<Artwork>(
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final),
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final)
        ,
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final)
        ,
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final)
        ,
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final),
        Artwork("한국", image = R.drawable.sowoon_bg),
        Artwork("한국",image = R.drawable.logo),
        Artwork("한국",image = R.drawable.sowoon_bg ),
        Artwork("한국",image = R.drawable.logo_final)

    )
}

