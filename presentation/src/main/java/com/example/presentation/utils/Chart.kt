package com.example.presentation.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun chart(
    xData: List<String>,
    yData: List<Float>,
    dataLabel: String,
    modifier: Modifier = Modifier,
){
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                xAxis.apply {
                    axisMinimum = 0f
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f // x축의 값 간 최소 간격 설정
                    isGranularityEnabled = true
                    valueFormatter = object : ValueFormatter(){
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt() // 소수점 제거하고 인덱스로 사용
                            return if (index in 0 until xData.size) {
                                xData[index] // 인덱스에 해당하는 날짜 반환
                            } else {
                                "" // 유효하지 않은 인덱스는 빈 문자열 반환
                            }
                        }

                    }
                }


                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                    valueFormatter = object : ValueFormatter(){
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}만원"
                        }
                    }
                }
                axisRight.isEnabled = false
                legend.apply {
                    horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    orientation = Legend.LegendOrientation.HORIZONTAL
                }
                description.isEnabled = false
                // Enable touch gestures
                setTouchEnabled(true)
                isDragEnabled = true
                isScaleXEnabled = false
                isScaleYEnabled = false
                animateX(500, Easing.EaseInOutQuad)
                description.text = "가격 현황"
                description.textSize = 12f
            }
        },
        update = { chart ->
            if (xData.isNotEmpty() && yData.isNotEmpty()) {
                val entries = yData.mapIndexed { index, value ->
                    Entry(index.toFloat(), value)
                }

                val dataSet = LineDataSet(entries, dataLabel).apply {
                    circleRadius = 5f
                    valueTextSize = 12f
                    mode = LineDataSet.Mode.LINEAR
                }
                chart.data = LineData(dataSet)
                chart.invalidate() // 그래프를 다시 그리기
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun test121212(){
    chart(
        xData = listOf("2025-01-01", "2025-01-02", "2025-01-03"),
        yData = listOf(100f, 200f, 250f),
        dataLabel = "그래프",
        Modifier.background(Color.White),
    )
}