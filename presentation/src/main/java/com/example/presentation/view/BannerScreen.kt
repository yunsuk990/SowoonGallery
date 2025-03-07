package com.example.presentation.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.presentation.R
import com.example.presentation.utils.FullScreenArtwork
import com.example.presentation.viewModel.MainViewModel

@Composable
fun BannerScreen(navController: NavHostController, itemId: Int) {
    Scaffold(
        topBar = { BannerScreenTopBar(navController = navController) },
    ){ innerPadding ->
        if(itemId == 0){
            BannerFirst(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
        }else{
            BannerSecond(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
        }
    }
}

@Composable
fun BannerFirst(modifier: Modifier){
    var bannerList = listOf<Int>(R.drawable.sowoon_banner_exp1, R.drawable.sowoon_banner_exp4, R.drawable.sowoon_banner_exp3)
    val pagerState = rememberPagerState(pageCount = {bannerList.size}, initialPage = 0)
    val context = LocalContext.current
    val url = stringResource(R.string.booking_1)
    var imageTranslate by remember { mutableStateOf(false) }

    Box(modifier = modifier.verticalScroll(rememberScrollState())) {
        Column {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                modifier = Modifier.height(280.dp).fillMaxWidth()
            ) { page ->
                Image(
                    painter = painterResource(bannerList[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clickable {
                        imageTranslate = true
                    },
                    contentScale = ContentScale.Crop
                )
            }
            if(imageTranslate){
                FullScreenArtwork(imageUrls = bannerList, onClose = {imageTranslate = false})
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
                Text("도자기 페인팅", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Text("초벌되어 있는 도자기 위에 도자용 물감으로 채색하는 수업입니다. 컵, 접시, 그릇 등 다양한 품목에 원하는 그림을 그려 표현 할 수 있습니다.", fontSize = 14.sp, color = Color.Black)

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("이용안내", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("- 공방 여건상 도자기 페인팅 체험은 평일에만 개설", fontSize = 14.sp, color = Color.Gray)
                    Text("- 도자기 특성상 가마소성(굽는)시간이 걸려 약 2주에서 3주정도 소요", fontSize = 14.sp, color = Color.Gray)
                    Text("- 완성 후 방문 픽업 또는 택배 신청 가능", fontSize = 14.sp, color = Color.Gray)
                    Text("- 도자기 건조 및 소성 과정에서 반점 갈라짐 휨 등 변화가 있을 수 있음", fontSize = 14.sp, color = Color.Gray)
                    Text("- 체험소요 시간은 1시간 30분에서 2시간 정도 소요가 되며 이는 기물의 크기와 종류, 사람에 따라 조금씩 변동", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

        }
        Button(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(15.dp).align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            contentPadding = PaddingValues(vertical = 15.dp)
        ) {
            Text("예약하기", color = Color.White)
        }
    }
}

@Composable
fun BannerSecond(modifier: Modifier){
    var bannerList = listOf<Int>(R.drawable.sowoon_banner_exp2, R.drawable.sowoon_room)
    val pagerState = rememberPagerState(pageCount = {bannerList.size}, initialPage = 0)
    var imageTranslate by remember { mutableStateOf(false) }

    Box(modifier = modifier.verticalScroll(rememberScrollState())) {
        Column {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                modifier = Modifier.height(280.dp).fillMaxWidth()
            ) { page ->
                Image(
                    painter = painterResource(bannerList[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clickable {
                        imageTranslate = true
                    },
                    contentScale = ContentScale.Crop
                )
            }
            if(imageTranslate){
                FullScreenArtwork(imageUrls = bannerList, onClose = {imageTranslate = false})
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
                Text("그림 페인팅", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Text("흰 백지 위에 물감으로 그림 그리는 수업입니다. 컵, 접시, 그릇 등 다양한 품목에 원하는 그림을 그려 표현 할 수 있습니다.", fontSize = 14.sp, color = Color.Black)

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("이용안내", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("- 공방 여건상 그림 페인팅 체험은 평일에만 개설", fontSize = 14.sp, color = Color.Gray)
                    Text("- 완성한 작품은 가져 갈 수 있음", fontSize = 14.sp, color = Color.Gray)
                    Text("- 체험소요 시간은 1시간 30분에서 2시간 정도 소요가 되며 이는 기물의 크기와 종류, 사람에 따라 조금씩 변동", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

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
        BannerSecond(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
    }
}
