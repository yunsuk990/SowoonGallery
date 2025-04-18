package com.yschoi.presentation.utils

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isUnspecified

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = style.color
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember { mutableStateOf(false) }

    val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize
    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if(shouldDraw){
                drawContent()
            }
        },
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if(result.didOverflowWidth) {
                if(style.fontSize.isUnspecified){
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = defaultFontSize
                    )
                }
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize =  resizedTextStyle.fontSize * 0.95
                )
            }else{
                shouldDraw = true
            }
          }
    )
}