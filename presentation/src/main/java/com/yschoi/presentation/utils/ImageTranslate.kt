package com.yschoi.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun <T> FullScreenArtwork(imageUrls: List<T>, onClose: () -> Unit){
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)){
        var state = rememberPagerState(initialPage = 0, pageCount = { imageUrls.size })
        val zoomState = rememberZoomState(
            maxScale = 5f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 확대 가능한 이미지
            HorizontalPager(
                state = state,
                pageSize = PageSize.Fill,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                userScrollEnabled = true
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = "확대 가능한 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .zoomable(zoomState),
                    contentScale = ContentScale.FillWidth
                )
            }

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 25.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(imageUrls.size) { iteration ->
                    val color = if(state.currentPage == iteration) Color.Black else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
            // 닫기 버튼
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "닫기",
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview
@Composable
fun imageTest(){
    FullScreenArtwork(
        imageUrls = listOf("https://upload.wikimedia.org/wikipedia/commons/a/a9/221125_%EC%B2%AD%EB%A3%A1%EC%98%81%ED%99%94%EC%83%81_%EB%A0%88%EB%93%9C%EC%B9%B4%ED%8E%AB_01_%28cropped%29.jpg"),
        onClose = {},
    )
}