package com.yschoi.presentation.view.Setting

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yschoi.presentation.R
import com.yschoi.presentation.view.ui.theme.SowoonTheme

class AnnounceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SowoonTheme{
                AnnounceScreen()
            }
        }
    }
}

@Composable
fun AnnounceScreen(){
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AnnounceTopAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnounceTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "공지사항", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "뒤로가기"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            actions = {},
        )
        Divider(thickness = 0.5.dp, color = Color.LightGray)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnnounceTest() {
    SowoonTheme {
        AnnounceScreen()
    }
}