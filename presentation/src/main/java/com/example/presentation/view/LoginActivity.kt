package com.example.presentation.view

import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.AuthState
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.LoginViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

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

                //storedVerificationId
                val storedVerificationId by viewModel.storedVerificationId.collectAsState()

                //resendToken
                val resendToken by viewModel.resendToken.collectAsState()

                // 최종가입 성공 유무
                val authState by viewModel.authState.observeAsState()

                val keyboardController = LocalSoftwareKeyboardController.current


                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            keyboardController?.hide()
                        }
                        .background(Color.White),
                ) {
                    LoginScreen(
                        activityFinish = { finish() },
                        registerBtnOnClick = {
                            domainUser -> viewModel.saveUserInfoRTD(domainUser)
                        },
                        authState = authState,
                        verifyBtnOnClick = { verifyNumber ->
                            keyboardController?.hide()
                            if(storedVerificationId.isNotEmpty()){
                                val credential = PhoneAuthProvider.getCredential(storedVerificationId, verifyNumber)
                                viewModel.signInWithPhoneAuthCredential(credential)
                            }
                        },
                        verifyPhoneNumber = { phoneNumber ->
                            keyboardController?.hide()
                            viewModel.verifyPhoneNumber("+82 ${phoneNumber.removeRange(0,1)}", this@LoginActivity)
                        },
                        resendVerifyPhoneNumber = { phoneNumber ->
                            keyboardController?.hide()
                            viewModel.resendVerifyPhoneNumber("+82 ${phoneNumber.removeRange(0,1)}", this@LoginActivity)
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun LoginScreen(
    activityFinish: () -> Unit,
    registerBtnOnClick: (DomainUser) -> Unit,
    authState: AuthState?,
    verifyPhoneNumber: (String) -> Unit,
    verifyBtnOnClick: (String) -> Unit,
    resendVerifyPhoneNumber: (String) -> Unit,
){

    val numberFocusRequester = remember { FocusRequester() }
    val verifyCodeRequester = remember { FocusRequester() }

    //핸드폰 번호
    var phoneNumber by rememberSaveable { mutableStateOf("") }

    //인증번호
    var verifyNumber by rememberSaveable { mutableStateOf("") }

    //핸드폰 번호 입력 옵저블
    var phoneNumberVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LaunchedEffect(Unit) {
            numberFocusRequester.requestFocus()
        }

        LoginScreenTopBar()

        if(phoneNumberVisible){
            Text(text = "안녕하세요!\n휴대폰 번호로 로그인해주세요.", fontWeight = FontWeight.Bold, lineHeight = 30.sp, fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp, horizontal = 15.dp))
        }

        if(authState !is AuthState.NewUser){
            //번호 등록
            outlineTextField(
                labelText = "휴대폰 번호(- 없이 숫자만 입력)",
                text = phoneNumber,
                onValueChange = { newText ->
                    if (newText.length <= 11) {
                        phoneNumber = newText
                    }
                },
                supportingText = {},
                isError = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.focusRequester(numberFocusRequester)
            )

            //인증 문자 전송 버튼
            outLinedButton(
                buttonText = if(phoneNumberVisible) "인증문자 받기" else "인증번호 재전송", isEnabled = phoneNumber.length == 11,
                onClick = {
                    Log.d("sms_phone", PhoneNumberUtils.formatNumber("+82${phoneNumber.removeRange(0,1)}", "KR").toString())
                    Log.d("phoneNumberVisible",phoneNumberVisible.toString())
                    if(phoneNumberVisible){
                        verifyPhoneNumber(phoneNumber)
                    } else {
                        resendVerifyPhoneNumber(phoneNumber)
                    }

                    phoneNumberVisible = false
                }
            )

            if(!phoneNumberVisible){
                Spacer(modifier = Modifier.height(20.dp))
                LaunchedEffect(Unit) {
                    verifyCodeRequester.requestFocus()
                }
                //인증 번호 입력
                outlineTextField(
                    text = verifyNumber,
                    labelText = "인증번호 입력",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { newCode -> verifyNumber = newCode},
                    supportingText = {
                        if(authState is AuthState.Error) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(Icons.Filled.Info, modifier = Modifier.size(18.dp), contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.padding(start = 3.dp))
                                Text(authState.message, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                        }
                    },
                    isError = authState is AuthState.Error,
                    modifier = Modifier.focusRequester(verifyCodeRequester)
                )

                //인증 진행
                outLinedButton(
                    buttonText = "인증번호 확인",
                    isEnabled = verifyNumber.length >= 4,
                    onClick = {
                        //인증번호 확인
                        verifyBtnOnClick(verifyNumber)
                    }
                )
            }
        }
        HandleAuthState(
            authState = authState,
            activityFinish = activityFinish,
            registerForm = { uid ->
                registerUI(
                    registerBtnOnClick = { domainUser ->
                        domainUser.uid = uid
                        registerBtnOnClick(domainUser)
                    }
                )
            }

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenTopBar(){
    var context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = {
                (context as Activity).finish()
            }) { Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기") }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        actions = {}
    )
}

@Composable
fun HandleAuthState(
    authState: AuthState?,
    activityFinish: () -> Unit,
    registerForm: @Composable (String) -> Unit,
){
    when(authState){
        AuthState.Authenticated -> {
            Log.d("AuthState", "Authenticated")
            activityFinish()
        }
        is AuthState.NewUser -> {
            registerForm(authState.uid)

        }
        AuthState.ExistUser -> {
            Log.d("AuthState", "ExistUser")
            activityFinish()
        }
        is AuthState.Error -> {
            val message = authState.message
            Log.d("AuthState_Error", message)
            //viewModel.resetAuthState()
        }
        is AuthState.Loading -> {
            CircularProgressIndicator()
        }
        else -> {

        }
    }
}

@Composable
fun SingleChoiceSegmentedButton(selectedIndex: Int, indexOnChange: (Int) -> Unit) {
    val options = listOf("남자", "여자")


    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        options.forEachIndexed { index, label ->
            var bgcolor = if(selectedIndex == index) Color.LightGray else Color.White
            var textColor = if(selectedIndex == index) Color.White else Color.Black


            Button(
                shape = RoundedCornerShape(5.dp),
                onClick = { indexOnChange(index) },
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = bgcolor
                ),
                border = BorderStroke(width = 1.dp, color = textColor)

            ) { Text(label, color = textColor)}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun registerUI(
    registerBtnOnClick: (DomainUser) -> Unit,
){

    var userName by remember { mutableStateOf("") }
    var userOld by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate: String = datePickerState.selectedDateMillis?.let {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        formatter.format(Date(it))
    } ?: ""

    var selectedIndex by remember { mutableIntStateOf(0) }

    Column() {
        Text("회원가입", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 15.dp), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(40.dp))

        outlineTextField(
            text = userName,
            labelText = "이름",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { newText -> userName = newText },
            supportingText = null,
            isError = false,
            modifier = Modifier
        )

        SingleChoiceSegmentedButton(selectedIndex, {index -> selectedIndex = index})

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("태어난 날짜") },
            textStyle = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            colors = TextFieldDefaults.colors(cursorColor = Color.Black, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Black, focusedLabelColor = Color.Black, focusedContainerColor = Color.White,),
            supportingText = {},
            leadingIcon = {
                IconButton( onClick = { showDatePicker = true}) { Icon(Icons.Filled.DateRange, contentDescription = null) }
            },
            isError = false
        )

        if(showDatePicker){
            Popup(
                onDismissRequest = { showDatePicker = false },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .padding(15.dp)
                        .shadow(elevation = 4.dp)

                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = colorResource(R.color.lightwhite),
                        ),
                        title = {}
                    )
                }
            }
        }

        outlineTextField(
            text = userOld,
            labelText = "나이",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { newText -> userOld = newText },
            supportingText =  {},
            isError = false,
            modifier = Modifier
        )
        outLinedButton(
            buttonText = "가입하기",
            isEnabled = userName.isNotEmpty() && userOld.isNotEmpty() && selectedDate.isNotEmpty() && userOld.isNotEmpty(),
            onClick = {
                registerBtnOnClick(
                    DomainUser(name = userName, age = userOld.toInt(), sex = selectedIndex, birth = selectedDate)
                )
            }
        )
    }
}

@Composable
fun outlineTextField(
    text: String,
    labelText: String,
    keyboardOptions: KeyboardOptions,
    onValueChange: (String) -> Unit,
    supportingText: @Composable() (() -> Unit)?,
    isError: Boolean,
    modifier: Modifier
){
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = labelText) },
        textStyle = TextStyle(fontSize = 20.sp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 15.dp, end = 15.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Black,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Black,
            focusedLabelColor = Color.Black,
            focusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            errorIndicatorColor = Color.Red,
            errorLabelColor = Color.Red,
        ),
        isError = isError ,
        supportingText = {
            if(isError){
                supportingText!!()
            }
        },
        keyboardOptions = keyboardOptions
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
        border = if(isEnabled) BorderStroke(width = 0.5.dp, color = Color.Black) else null,
        colors = if(isEnabled) ButtonDefaults.filledTonalButtonColors(Color.White) else ButtonDefaults.filledTonalButtonColors(Color.Gray)
    ) {
        Text(text = buttonText, color = if(isEnabled) Color.Black else Color.White, fontSize = 16.sp, modifier = Modifier.padding(0.dp))
    }
}

@Preview
@Composable
fun LoginActivityTest(){
    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LoginScreen(
                activityFinish = {},
                registerBtnOnClick = {},
                authState = AuthState.Loading,
                verifyPhoneNumber = {},
                verifyBtnOnClick = {},
                resendVerifyPhoneNumber = {},
            )
        }

    }
}
