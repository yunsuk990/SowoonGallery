package com.example.presentation.view

import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.model.AuthState
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.LoginViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )
            SowoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {

                    MainScreen(viewModel, this)
                }
            }
        }
    }
}
@Composable
private fun MainScreen(
    viewModel: LoginViewModel,
    activity: Activity
){
    //핸드폰 번호 입력 옵저블
    var phoneNumberVisible by rememberSaveable { mutableStateOf(true) }

    //핸드폰 번호
    var phoneNumber by rememberSaveable { mutableStateOf("") }

    //인증번호
    var verifyNumber by rememberSaveable { mutableStateOf("") }

    // 최종가입 성공 유무
    val authState by viewModel.authState.observeAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 20.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(onClick = { activity.finish() }) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "back", modifier = Modifier.size(35.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            if(phoneNumberVisible){
                Text(text = "안녕하세요!\n휴대폰 번호로 로그인해주세요.", fontWeight = FontWeight.Bold, lineHeight = 30.sp, fontSize = 20.sp, modifier = Modifier
                    .padding(start = 15.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            PhoneNumberInput(phoneNumber){ newNumber -> phoneNumber = newNumber}

            PhoneVerificationButton(activity,phoneNumberVisible, phoneNumber, viewModel){ phoneNumberVisible = false}

            if(!phoneNumberVisible){
                Log.d("Verification", "true")
                Spacer(modifier = Modifier.height(10.dp))
                VerificationInput(verifyNumber){ newCode -> verifyNumber = newCode }
                VerifyButton(viewModel,verifyNumber, activity)
            }

            HandleAuthState(activity, authState, viewModel)
        }
        if (authState is AuthState.Loading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
    }
}

@Composable
fun HandleAuthState(activity: Activity, authState: AuthState?, viewModel: LoginViewModel) {
    when(authState){
        AuthState.Authenticated -> {
            Log.d("AuthState", "Authenticated")
            Toast.makeText(activity, "가입 완료", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
        is AuthState.NewUser -> {
            Log.d("AuthState", "NewUser")
            val uid = authState.uid
            registerUI(viewModel = viewModel, uid)
        }
        AuthState.ExistUser -> {
            Log.d("AuthState", "ExistUser")
            Toast.makeText(activity, "로그인 완료", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
        is AuthState.Error -> {
            Log.d("AuthState", "error")
            val message = authState.message
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            viewModel.resetAuthState()
        }
        else -> {

        }
    }
}

@Composable
fun VerifyButton(viewModel: LoginViewModel, verifyNumber: String, activity: Activity) {
    outLinedButton(
        buttonText = "인증번호 확인",
        isEnabled = true,
        onClick = {
            //인증번호 확인
            var a = verifyNumber
            val credential = PhoneAuthProvider.getCredential(viewModel.storedVerificationId, a).apply {
                Log.d("credential", "called")
            }
            viewModel.signInWithPhoneAuthCredential(credential)
        }
    )
}

@Composable
fun VerificationInput(verifyNumber: String, onValueChange: (String) -> Unit) {
    outlineTextField(
        labelText = "인증번호 입력",
        text = verifyNumber,
        onValueChange = onValueChange
    )
}

@Composable
fun PhoneVerificationButton(
    activity: Activity,
    phoneNumberVisible: Boolean,
    phoneNumber: String,
    viewModel: LoginViewModel,
    onHide: () -> Unit
) {
    outLinedButton(
        buttonText = if(phoneNumberVisible) "인증문자 받기" else "인증번호 재전송", isEnabled = phoneNumber.length >= 10,
        onClick = {
            Log.d("sms_phone", PhoneNumberUtils.formatNumber("+82${phoneNumber.removeRange(0,1)}", "KR").toString())
            Log.d("phoneNumberVisible",phoneNumberVisible.toString())
            if(phoneNumberVisible){
                viewModel.verfiyPhoneNumber("+82 ${phoneNumber.removeRange(0,1)}", activity)
            }else{
                viewModel.resendVerifyPhoneNumber("+82 ${phoneNumber.removeRange(0,1)}", activity)
            }
            onHide()
        }
    )
}


@Composable
fun PhoneNumberInput(phoneNumber: String, onValueChange: (String) -> Unit) {
    outlineTextField(
        labelText = "휴대폰 번호(- 없이 숫자만 입력)",
        text = phoneNumber,
        onValueChange = { newText ->
            if (newText.length <= 11) {
                onValueChange(newText)
            }
        }
    )
}

@Composable()
fun registerUI(viewModel: LoginViewModel, uid: String){
    var userName by rememberSaveable { mutableStateOf("") }
    var userOld by rememberSaveable { mutableStateOf("") }
    outlineTextField(labelText = "이름", text = userName, onValueChange = { name -> userName = name})
    outlineTextField(labelText = "나이", text = userOld, onValueChange = { old -> userOld = old})
    outLinedButton(buttonText = "가입하기", isEnabled = userName.isNotEmpty() && userOld.isNotEmpty(), onClick = {
        viewModel.saveUserInfoRTD(uid, userName, userOld)
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun outlineTextField(labelText: String, text: String, onValueChange : (String) -> Unit){
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
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
