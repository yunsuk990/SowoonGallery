package com.example.presentation.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.R

@Composable
fun ArtInfo(alpha: Float) {
//    var isVisible by remember { mutableStateOf(true) }
//    val alpha by animateFloatAsState(
//        targetValue = if (isVisible) 1f else 0f, // alpha 값 전환
//        animationSpec = tween(durationMillis = 1000) // 애니메이션 속도 설정
//    )
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
    ) {
        Row {
            Image(painter = painterResource(id = R.drawable.artist_profile), contentScale = ContentScale.FillWidth, contentDescription = "작가사진", modifier = Modifier
                .size(180.dp)
                .alpha(alpha))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween // 텍스트 간 간격 조정
            ) {
                Text(text = "素暈   정은숙", fontSize = 18.sp, modifier = Modifier.fillMaxWidth().alpha(alpha), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "홍익대학교 미술대학 졸업(1987년)", fontSize = 12.sp,  modifier = Modifier.fillMaxWidth().alpha(alpha), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "수상경력", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().alpha(alpha), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "대한민국미술대전, 경기미술대전, 남농 미술대전, 목우회 등", fontSize = 12.sp, modifier = Modifier.fillMaxWidth().alpha(alpha), textAlign = TextAlign.Start)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "소울갤러리 대표\n한국미협, 동양수묵연구원, 파인아트클럽, 아태미협회원", fontSize = 12.sp, modifier = Modifier.fillMaxWidth().alpha(alpha), textAlign = TextAlign.Start)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween // 텍스트 간 간격 조정
        ) {
            Text(text = "주요 전시", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(alpha))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
            Text(text = "2023   제 4회 개인전 / 인사아트프라자 서울", fontSize = 12.sp, modifier = Modifier.alpha(alpha))
        }
    }



}

@Preview(showSystemUi = true)
@Composable
fun ArtInfoTest(){
    Surface(modifier = Modifier.background(Color.White)) {
        ArtInfo(1.0f)
    }
}
