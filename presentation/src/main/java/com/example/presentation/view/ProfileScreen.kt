package com.example.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.R
import com.example.presentation.model.Artwork

@Preview(showSystemUi = true)
@Composable
fun ProfileScreen(){
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var list = arrayListOf("Home", "Work", "Travel", "Fitness", "Books", "Music", "Games")
    var artworks = arrayListOf<Artwork>(
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

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
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
            verticalItemSpacing = 10.dp
        )

//        LazyVerticalGrid(
//            contentPadding = PaddingValues(8.dp),
//            columns = GridCells.Fixed(2),
//            state = rememberLazyGridState(),
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//        ){
//            itemsIndexed(items){index, artwork ->
//                artworkCard(artwork = artwork)
//
//            }
//        }


    }
}

@Composable
fun artworkCard(artwork: Artwork){
    Card(){
        Image(
            painter = painterResource(id = artwork.image),
            contentDescription = null,
            contentScale = ContentScale.Crop
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


