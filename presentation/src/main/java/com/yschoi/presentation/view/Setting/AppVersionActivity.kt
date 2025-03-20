package com.yschoi.presentation.view.Setting

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yschoi.presentation.R
import com.yschoi.presentation.view.ui.theme.SowoonTheme

class AppVersionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName

        setContent {
            SowoonTheme {
                AppVersionScreen(versionName = versionName)
            }
        }
    }
}

@Composable
fun AppVersionScreen(versionName: String) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AppVersionTopAppBar()
        Column(modifier = Modifier.padding(start = 15.dp, top = 30.dp)) {
            Text(text = "버전정보", fontSize = 16.sp, color = Color.Black)

            Row(
                modifier = Modifier.padding(top = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(70.dp).padding(5.dp)){
                    Image(painter = painterResource(R.drawable.app_market), contentDescription = null, modifier = Modifier.size(65.dp))
                }
                Column(modifier = Modifier.padding(start = 15.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("소운", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("ver. $versionName", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppVersionTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "앱 관리", style = MaterialTheme.typography.titleMedium) },
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
fun AppVersionTest() {
    SowoonTheme {
        AppVersionScreen("1.0")
    }
}