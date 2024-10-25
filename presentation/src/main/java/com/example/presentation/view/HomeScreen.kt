package com.example.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.presentation.R

@Composable
fun HomeScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pagerState = rememberPagerState (pageCount = {
            3
        })
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill
        ) {
            Image(
                painter = painterResource(id = R.drawable.sowoon_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth)
        }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
//                    .align(Alignment.Center)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount){iteration ->
                val color = if(pagerState.currentPage == iteration) Color.Black else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)
                )

            }
        }
    }
}

@Composable
@Preview
fun Test(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pagerState = rememberPagerState (pageCount = {
                3
            })
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sowoon_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth)
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
//                    .align(Alignment.Center)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount){iteration ->
                    val color = if(pagerState.currentPage == iteration) Color.Black else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(10.dp)
                    )

                }
            }
        }

    }
}