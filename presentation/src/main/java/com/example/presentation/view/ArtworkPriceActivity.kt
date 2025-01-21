package com.example.presentation.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.lightWhite
import com.example.presentation.utils.chart
import com.example.presentation.view.ui.theme.SowoonTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ArtworkPriceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SowoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArtworkPriceScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkPriceTopBar(){
    var context = LocalContext.current

    CenterAlignedTopAppBar(
        title = { Text(text = "가격제시 현황", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = { IconButton(onClick = {
            (context as Activity).finish()
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
        }
        },
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun priceInputDialog(priceTextField: String, onDismissRequest: () -> Unit, onInput: (String) -> Unit, onAddData: () -> Unit){
    AlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = { onDismissRequest() },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(
                    onClick = onAddData,
                    border = BorderStroke(0.5.dp, Color.LightGray),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "제시하기")
                }

                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    border = BorderStroke(0.5.dp, Color.LightGray),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(text = "취소하기")
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
        title = { Text(text = "가격 제시", fontSize = 20.sp, fontWeight = FontWeight.Bold ,modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)},
        text = {
                BasicTextField(
                    value = priceTextField,
                    onValueChange = onInput,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = lightWhite,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(8.dp)
                        ) {
                            innerTextField()
                        }
                    },
                )

        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun ArtworkPriceScreen(){
    // x축 (날짜)와 y축 (가격) 데이터를 관리
    var xData by remember { mutableStateOf(mutableListOf<String>("01-01","01-02")) }
    var yData by remember { mutableStateOf(mutableListOf<Float>(10f,20f)) }

    val dateIndexMap = remember { mutableStateMapOf<Float, String>() } // x축 매핑

    var priceTextField by remember { mutableStateOf("") }
    var priceInputBtn by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column {
        ArtworkPriceTopBar()
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        ){
            Column {
                chart(xData = xData, yData = yData, dataLabel = "가격현황", modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(all = 8.dp),
                    dateIndexMap
                )
            }

            TextButton(
                onClick = {
                    priceInputBtn = true
                },
                border = BorderStroke(width = 0.5.dp, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp)
            ) {
                Text(text = "가격 제시하기", fontSize = 18.sp, color = Color.Black)
            }
            if(priceInputBtn){
                priceInputDialog(priceTextField, {priceInputBtn = false}, { priceTextField = it}, onAddData = {
                    val x = priceTextField.toFloatOrNull()
                    if(x != null){
                        val currentDate = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd") // 원하는 날짜 형식
                        val formattedDate = currentDate.format(formatter)
                        dateIndexMap[xData.size - 1f] = formattedDate // 인덱스 매핑
                        xData.add(formattedDate)
                        yData.add(priceTextField.toFloat())
                        Log.d("Xdata", "$xData:$yData")
                        priceTextField = ""
                        priceInputBtn = false
                    }else{
                        Toast.makeText(context, "가격을 기입해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } )
            }
        }
    }
}
