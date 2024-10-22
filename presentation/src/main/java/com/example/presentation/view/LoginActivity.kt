package com.example.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class LoginActivity : ComponentActivity() {
    //val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SowoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    MainScreen(LoginViewModel(application), this)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

@Composable
private fun MainScreen(
    viewModel: LoginViewModel,
    activity: Activity
){
    val context = LocalContext.current
    var phoneNumberVisible by rememberSaveable { mutableStateOf(true) }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var verifyNumber by rememberSaveable { mutableStateOf("") }
    var isButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isVerifyButtonEnabled by rememberSaveable { mutableStateOf(false) }

    //requestSmsPermission(context)

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(top = 20.dp)
    ) {
        IconButton(onClick = {  }) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "back",
                modifier = Modifier.size(35.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        
        if(phoneNumberVisible){
            Text(
                text = "안녕하세요!\n휴대폰 번호로 로그인해주세요.",
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .alpha(if (phoneNumberVisible) 1f else 0f),
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        outlineTextField(
            labelText = "휴대폰 번호(- 없이 숫자만 입력)",
            text = phoneNumber,
            onValueChange = { newText ->
                phoneNumber = newText
                isButtonEnabled = newText.length == 11
            }
        )

        outLinedButton(
            buttonText = "인증문자 받기",
            isEnabled = isButtonEnabled,
            onClick = {
                phoneNumberVisible = false
                viewModel.verfiyPhoneNumber(Firebase.auth, phoneNumber, activity)
            }
        )

        if(!phoneNumberVisible){
            Spacer(modifier = Modifier.height(10.dp))
            outlineTextField(
                labelText = "인증번호 입력",
                text = verifyNumber,
                onValueChange = {newText ->
                    verifyNumber = newText
                    isVerifyButtonEnabled = newText.length >= 4

            })

            outLinedButton(
                buttonText = "인증번호 확인",
                isEnabled = isVerifyButtonEnabled,
                onClick = {
                    //인증번호 확인

                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun outlineTextField(labelText: String, text: String, onValueChange : (String) -> Unit){
    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            if(newText.length <= 11){
                onValueChange(newText)
            }
        },
        label = { Text(text = labelText) },
        textStyle = TextStyle(fontSize = 20.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
            .height(60.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    )
}

@Composable
fun outLinedButton(buttonText: String, isEnabled: Boolean, onClick: () -> Unit){
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 10.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        enabled = isEnabled,
        colors = if(isEnabled) ButtonDefaults.filledTonalButtonColors(Color.White) else ButtonDefaults.filledTonalButtonColors(Color.Gray)
    ) {
        Text(text = buttonText, color = if(isEnabled) Color.Black else Color.White, fontSize = 16.sp, modifier = Modifier.padding(0.dp))
    }
}

fun requestSmsPermission(context: Context) {
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.RECEIVE_SMS,
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity, arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS), 1
        )
    }
}

