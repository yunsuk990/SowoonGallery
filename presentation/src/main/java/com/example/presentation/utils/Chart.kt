package com.example.presentation.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun chart(
    xData: List<Float>,
    yData: List<Float>,
    dataLabel: String,
    modifier: Modifier = Modifier
){
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val chart = LineChart(context)  // Initialise the chart
            val entries: List<Entry> = xData.zip(yData) { x, y -> Entry(x, y) }  // Convert the x and y data into entries
            val dataSet = LineDataSet(entries, dataLabel).apply {
                setDrawFilled(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }  // Create a dataset of entries
            chart.data = LineData(dataSet)  // Pass the dataset to the chart
            // Refresh and return the chart

            chart.xAxis.apply {
                setDrawGridLines(false)
            }

            chart.axisLeft.apply {
                setDrawGridLines(false)
            }


            // Enable touch gestures
            chart.setTouchEnabled(true)
            chart.isDragEnabled = true
            chart.isScaleXEnabled = false
            chart.isScaleYEnabled = false


            chart.invalidate()
            chart
        },
        update = { chart ->
            val entries = xData.zip(yData) { x, y -> Entry(x, y) }
            val dataSet = LineDataSet(entries, dataLabel)
            chart.data = LineData(dataSet)
            chart.invalidate() // 그래프 다시 그리기
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun test121212(){
    chart(xData = listOf(1f,2f,3f), yData = listOf(1f,2f,3f), dataLabel = "그래프")
}