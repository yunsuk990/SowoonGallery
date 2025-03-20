package com.yschoi.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yschoi.presentation.utils.noRippleClickable
import com.yschoi.presentation.view.ui.theme.SowoonTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import com.yschoi.presentation.R

@AndroidEntryPoint
class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )
            SowoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScreen(){
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val logoPainter = painterResource(R.drawable.logo)
            Image(
                painter = logoPainter,
                contentDescription = "Background",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column (
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ){

            filledTonalButtonExample {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 15.dp)
            ){
                Text(text = "이미 계정이 있나요?", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "로그인", color = Color.Black, fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp).noRippleClickable {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    })
            }
        }
    }
}

@Composable
fun filledTonalButtonExample(onClick: () -> Unit) {
    FilledTonalButton(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        colors = ButtonDefaults.filledTonalButtonColors(Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(12.dp),
    ) {
        Text("시작하기", fontSize = 16.sp, color = Color.Black)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StartActivityTest(){
    MainScreen()
}

