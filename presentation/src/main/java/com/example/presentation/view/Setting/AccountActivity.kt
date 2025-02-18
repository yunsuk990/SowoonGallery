package com.example.presentation.view.Setting

import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.view.ui.theme.SowoonTheme
import com.google.gson.Gson

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent.getStringExtra("userInfo")
        val userInfo = Gson().fromJson(intent, DomainUser::class.java)

        setContent {
            SowoonTheme {
                AccountScreen(userInfo = userInfo)
            }
        }
    }
}

@Composable
fun AccountScreen(userInfo: DomainUser){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
       AccountTopAppBar()
       Row(
           modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 15.dp),
           horizontalArrangement = Arrangement.spacedBy(8.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
           textField(
               value = PhoneNumberUtils.formatNumber(userInfo.phoneNumber, "KR"),
               label = "전화번호",
               modifier = Modifier.weight(5f)
           )
           Button(
               shape = RoundedCornerShape(8.dp),
               onClick = {},
               colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
               modifier = Modifier.weight(2f),
               contentPadding = PaddingValues(vertical = 10.dp)
           ) { Text("변경하기") }
       }

    }
}


@Composable
fun textField(value: String, label: String, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = Color.White,
            disabledLabelColor = Color.Black,
            disabledTextColor = Color.Black,
            disabledBorderColor = Color.Black,
        ),
        label = { Text(text = label) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "계정 관리", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "뒤로가기"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            actions = {},
        )
        Divider(thickness = 0.5.dp, color = Color.LightGray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview3() {
    SowoonTheme {
        AccountScreen(userInfo =  DomainUser(phoneNumber = "01099016074"))
    }
}