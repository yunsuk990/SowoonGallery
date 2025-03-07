package com.example.presentation.view.Setting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.view.ui.theme.SowoonTheme
import com.google.gson.Gson

class QuestionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userIntent = intent.getStringExtra("userInfo")
        val userInfo: DomainUser = Gson().fromJson(userIntent, DomainUser::class.java)

        setContent {
            SowoonTheme{
                QuestionActivityScreen(
                    userInfo = userInfo,
                    btnOnClick =  { intent ->
                        Log.d("QuestionActivity", "btnOnClick: ${intent}")
                        try {
                            startActivity(intent)
                        }catch (e: ActivityNotFoundException){
                            Toast.makeText(this, "이메일 앱 설치 후 이용해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionActivityScreen(userInfo: DomainUser, btnOnClick: (Intent) -> Unit) {

    var context = LocalContext.current
    var questionText by remember { mutableStateOf("") }
    var managerEmail = stringResource(id = R.string.manager_email)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        QuestionActivityTopAppBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp, vertical = 45.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = "보내는 사람: ${userInfo.email}",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.LightGray)
            OutlinedTextField(
                value = questionText,
                label = { Text("문의하기", color = Color.Gray) },
                singleLine = false,
                minLines = 10,
                maxLines = 10,
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                onValueChange = { newText -> questionText = newText},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val email = userInfo.email
                    if(email.isEmpty()){
                        Toast.makeText(context, "계정설정에서 이메일을 설정해주세요.", Toast.LENGTH_SHORT).show()
                    }else{
                        val uri = Uri.parse("mailto:$managerEmail") // 받는 사람
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        intent.putExtra(Intent.EXTRA_TEXT, "$questionText")
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Sowoon 문의: ")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        btnOnClick(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(vertical = 15.dp)
            ) {
                Text("문의하기", color = Color.White)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionActivityTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "문의하기", style = MaterialTheme.typography.titleMedium) },
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
fun QuestionActivityTest() {
    SowoonTheme {
        QuestionActivityScreen(DomainUser(email = "yunsuk990@naver.com"), btnOnClick = {})
    }
}