package com.example.presentation.utils

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
@UiComposable
fun AdView(
    modifier: Modifier = Modifier,
){
    val adRequest = AdRequest.Builder().build()
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/9214589741"
                loadAd(adRequest)
            }
        },
        update = { adview ->
            adview.loadAd(adRequest)
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdViewTest(){
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize()) {
        AdView(
            Modifier.fillMaxWidth()
        )
    }
}