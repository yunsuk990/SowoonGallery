package com.yschoi.presentation.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.yschoi.presentation.R
import com.yschoi.presentation.utils.FullScreenArtwork

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
                Text("도자기 페인팅 체험 클래스", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                //Text("초벌되어 있는 도자기 위에 도자용 물감으로 채색하는 수업입니다. 컵, 접시, 그릇 등 다양한 품목에 원하는 그림을 그려 표현할 수 있습니다.", fontSize = 14.sp, color = Color.Black)
                Text("나만의 감성을 담아 세상에 단 하나뿐인 도자기를 만들어보는 특별한 시간!\n" +
                        "도자기 페인팅 체험 클래스에 여러분을 초대합니다.", fontSize = 14.sp, color = Color.Black)

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDD8C 수업 소개", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("도자기 페인팅은 단순한 그림 그리기를 넘어, 직접 만든 작품을 일상에서 사용할 수 있는 특별한 경험입니다.", fontSize = 14.sp, color = Color.Gray)
                    Text("처음 경험하는 분들도 쉽게 따라 할 수 있도록 기초부터 차근차근 진행되며, 개성 넘치는 나만의 작품을 완성할 수 있도록 도와드립니다.", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDCCC 이런 분들에게 추천해요!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("✔\uFE0F 직접 만든 도자기로 특별한 의미를 더하고 싶은 분", fontSize = 14.sp, color = Color.Gray)
                    Text("✔\uFE0F 창의적인 활동을 통해 힐링하고 싶은 분", fontSize = 14.sp, color = Color.Gray)
                    Text("✔\uFE0F 특별한 기념품이나 선물을 직접 만들고 싶은 분", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83C\uDFA8 수업 내용", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("\uD83D\uDD39 연필로 밑그림 스케치 후 채색하기", fontSize = 14.sp, color = Color.Gray)
                    Text("\uD83D\uDD39 다양한 컬러와 패턴을 활용한 나만의 디자인 완성", fontSize = 14.sp, color = Color.Gray)
                    Text("\uD83D\uDD39 가마에서 단단하게 구워진 작품 수령", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDCE2 이용 안내", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    Text("\uD83D\uDD38 운영 시간", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("\t•\t 평일 10:00 ~ 15:00", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 약 1시간 반 ~ 2시간", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 사전 예약제로 운영", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\uD83D\uDD38 유의 사항", fontSize = 14.sp, color = Color.Gray,  fontWeight = FontWeight.Bold)
                    Text("\t•\t 도자기 특성상 가마소성(굽는)시간이 걸려 약 2주에서 3주정도 소요", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 완성 후 방문 픽업 또는 택배 신청 가능", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 도자기 건조 및 소성 과정에서 반점 갈라짐 휨 등 변화가 있을 수 있음", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(100.dp))

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
    val context = LocalContext.current
    val url = stringResource(R.string.booking_2)

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
                Text("그림 그리기 체험 클래스", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Text("예술적 감성을 깨우고, 나만의 작품을 만들어보는 특별한 시간!\n" +
                        "그림 그리기 수업에 여러분을 초대합니다.", fontSize = 14.sp, color = Color.Black)

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDD8C 수업 소개", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("이 수업은 그림을 처음 접하는 분들도 부담 없이 참여할 수 있도록 구성되었습니다. 선 하나 긋는 것부터 시작해 점점 나만의 개성을 담은 작품을 완성해 나갈 수 있도록 도와드립니다.", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDCCC 이런 분들에게 추천해요!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("✔\uFE0F 그림을 한 번도 그려본 적 없지만 도전해보고 싶은 분", fontSize = 14.sp, color = Color.Gray)
                    Text("✔\uFE0F 일상에서 벗어나 힐링할 시간을 찾고 계신 분", fontSize = 14.sp, color = Color.Gray)
                    Text("✔\uFE0F 손으로 직접 무언가를 그려보는 경험을 해보고 싶은 분", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83C\uDFA8 수업 내용", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("\uD83D\uDD39 기본 선과 명암 표현 익히기", fontSize = 14.sp, color = Color.Gray)
                    Text("\uD83D\uDD39 다양한 재료(연필, 색연필, 붓 등)를 활용한 그림 그리기", fontSize = 14.sp, color = Color.Gray)
                    Text("\uD83D\uDD39 감성을 담은 나만의 작품 완성하기", fontSize = 14.sp, color = Color.Gray)
                    Text("\uD83D\uDD39 완성한 작품을 기념으로 가져가기", fontSize = 14.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 5.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\uD83D\uDCE2 이용 안내", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    Text("\uD83D\uDD38 운영 시간", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("\t•\t 평일 10:00 ~ 15:00", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 총 2시간", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 사전 예약제로 운영", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\uD83D\uDD38 유의 사항", fontSize = 14.sp, color = Color.Gray,  fontWeight = FontWeight.Bold)
                    Text("\t•\t 준비물은 제공해 드립니다", fontSize = 14.sp, color = Color.Gray)
                    Text("\t•\t 그리고 싶으신 사진이나 그림등의 이미지 파일을 미리 정해서 가져오시는 것을 추천드립니다", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(100.dp))

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
        BannerFirst(modifier = Modifier.fillMaxSize().background(Color.White).padding(innerPadding))
    }
}
