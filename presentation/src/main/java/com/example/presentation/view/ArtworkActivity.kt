package com.example.presentation.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.R
import com.example.presentation.model.Artwork
import com.example.presentation.ui.theme.SowoonGalleryTheme

class ArtworkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val artwork: Artwork = intent.getParcelableExtra("artwork")!!

        setContent {
            SowoonGalleryTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    ArtworkScreen(artwork)
                }
            }
        }
    }
}

@Preview
@Composable
fun testUi(){
    ArtworkScreen(artwork = Artwork(title = "모나리자", image = R.drawable.sowoon_bg))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkScreen(artwork: Artwork){

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Log.d("screen_size", screenHeight.toString())


    Column{
        ArtworkTopBar(scrollBehavior)
        Image(
            painter = painterResource(id = artwork.image),
            contentDescription = artwork.title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            Text(text = artwork.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = artwork.date.toString(), fontSize = 14.sp)
            Text(text = artwork.title, fontSize = 14.sp)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTopBar(scrollBehavior: TopAppBarScrollBehavior){
    var context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text(text = "작품", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = { IconButton(onClick = {
            (context as Activity).finish()
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null, Modifier.padding(start = 5.dp))
        }},
        actions = {},
        scrollBehavior = scrollBehavior
    )
}

