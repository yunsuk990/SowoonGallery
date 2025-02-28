package com.example.presentation.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.presentation.R
import com.example.presentation.viewModel.MainViewModel

@Composable
fun BannerScreen(viewModel: MainViewModel, navController: NavHostController) {
    Scaffold(
        topBar = { BannerScreenTopBar(navController = navController) },
    ){ innerPadding ->
        BannerRoot(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
    }
}

@Composable
fun BannerRoot(modifier: Modifier){
    var bannerList = listOf<Int>(R.drawable.sowoon_banner_exp1, R.drawable.sowoon_banner_exp2)
    val pagerState = rememberPagerState(pageCount = {2}, initialPage = 0)


    Box(modifier = modifier) {
        Column {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
            ) { page ->
                Image(
                    painter = painterResource(bannerList[page]),
                    modifier = Modifier.height(220.dp).fillMaxWidth(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(bannerList.size) { iteration ->
                    Log.d("repeat", "called")
                    val color = if(pagerState.currentPage == iteration) Color.Black else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)
            ) {
                Text("도자기 페인팅", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text("초벌되어 있는 도자기 위에 도자용 물감으로 채색하는 수업입니다. 컵, 접시, 그릇 등 다양한 품목에 원하는 그림을 그려 표현 할 수 있습니다.", fontSize = 14.sp, color = Color.Gray)

                Row(modifier = Modifier.padding(top = 30.dp)) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("확인", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp)) {
                    Text("평일 2시간")
                }


            }

        }
        Button(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(15.dp).align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            onClick = {},
            contentPadding = PaddingValues(vertical = 15.dp)
        ) {
            Text("예약하기", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerScreenTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(text = "체험", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기")
            }
        },
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BannerTest(){
    Scaffold(
        topBar = { BannerScreenTopBar(navController = rememberNavController()) },
    ){ innerPadding ->
        BannerRoot(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
    }
}
