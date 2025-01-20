package com.example.presentation.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage

@Composable
fun FullScreenArtwork(imageUrl: String, onClose: () -> Unit){
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)){
        val scaleState = remember { mutableStateOf(1f) }
        val offsetState = remember { mutableStateOf(Offset.Zero) }
        var density = LocalDensity.current.density
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scaleState.value = (scaleState.value * zoom).coerceAtLeast(1f)
                        // 이동 제한: 이미지가 확대되었을 때 화면을 벗어나지 않도록 제한
                        val newOffsetX = offsetState.value.x + pan.x
                        val newOffsetY = offsetState.value.y + pan.y

                        // 화면을 벗어나지 않도록 x축과 y축 이동 제한
                        val maxOffsetX =
                            (scaleState.value - 1) * (density * 100) // 예시로, 화면의 100px을 기준으로 제한
                        val maxOffsetY = (scaleState.value - 1) * (density * 100)

                        offsetState.value = Offset(
                            x = newOffsetX.coerceIn(-maxOffsetX, maxOffsetX), // x축 이동 제한
                            y = newOffsetY.coerceIn(-maxOffsetY, maxOffsetY)  // y축 이동 제한
                        )
                    }
                }
        ) {
            // 확대 가능한 이미지
            AsyncImage(
                model = imageUrl,
                contentDescription = "확대 가능한 이미지",
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(
                        scaleX = scaleState.value,
                        scaleY = scaleState.value,
                        translationX = offsetState.value.x,
                        translationY = offsetState.value.y
                    )
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // 닫기 버튼
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun FullScreenArtwork(imageUrl: Int, onClose: () -> Unit){
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)){
        val scaleState = remember { mutableStateOf(1f) }
        val offsetState = remember { mutableStateOf(Offset.Zero) }
        var density = LocalDensity.current.density
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scaleState.value = (scaleState.value * zoom).coerceAtLeast(1f)
                        // 이동 제한: 이미지가 확대되었을 때 화면을 벗어나지 않도록 제한
                        val newOffsetX = offsetState.value.x + pan.x
                        val newOffsetY = offsetState.value.y + pan.y

                        // 화면을 벗어나지 않도록 x축과 y축 이동 제한
                        val maxOffsetX =
                            (scaleState.value - 1) * (density * 100) // 예시로, 화면의 100px을 기준으로 제한
                        val maxOffsetY = (scaleState.value - 1) * (density * 100)

                        offsetState.value = Offset(
                            x = newOffsetX.coerceIn(-maxOffsetX, maxOffsetX), // x축 이동 제한
                            y = newOffsetY.coerceIn(-maxOffsetY, maxOffsetY)  // y축 이동 제한
                        )
                    }
                }
        ) {
            // 확대 가능한 이미지
            Image(
                painter = painterResource(id = imageUrl),
                contentDescription = "확대 가능한 이미지",
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(
                        scaleX = scaleState.value,
                        scaleY = scaleState.value,
                        translationX = offsetState.value.x,
                        translationY = offsetState.value.y)
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
                )

            // 닫기 버튼
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        }
    }
}