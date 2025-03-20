package com.yschoi.presentation.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yschoi.presentation.R

@Composable
fun Banner(
    title: String,
    imageId: Int,
    modifier: Modifier,
    onClick: () -> Unit
){
    Box(
        modifier = modifier.fillMaxWidth().height(150.dp).noRippleClickable {
            onClick()
        }
    ){
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = painterResource(imageId),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.White,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomEnd).padding(15.dp)
        )



    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BannerTest(){
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Row {
                Banner(title = "도자기 컵 페인팅 체험", modifier = Modifier.fillMaxWidth(), imageId = R.drawable.sowoon_banner_exp1,
                    onClick = {})
            }
        }
    }
}