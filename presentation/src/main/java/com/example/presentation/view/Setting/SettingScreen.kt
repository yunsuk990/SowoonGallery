package com.example.presentation.view.Setting

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.Screen
import com.example.presentation.utils.LogOutToastMessage
import com.example.presentation.utils.LoginToastMessage
import com.example.presentation.view.StartActivity
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun  SettingScreen(viewModel: MainViewModel, navController: NavHostController){

    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    Log.d("SettingScreen", "userInfo: $userInfo")
    val context = LocalContext.current
    var requestLogin by remember { mutableStateOf(false) }
    var requestLogOut by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SettingTopAppBar(navController = navController) },
    ) { innerPadding ->
        SettingRoot(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            userInfo = userInfo,
            requestLogin = { requestLogin = true },
            actionBtn = {
                if(userInfo.uid.isEmpty()){
                    actionButton(text = "로그인", onClick = {
                        context.startActivity(Intent(context, StartActivity::class.java))
                    })
                }else{
                    actionButton(text = "로그아웃", onClick = {
                        requestLogOut = true
                    })
                }
            }
        )

        if(requestLogin){
            LoginToastMessage(
                dismissOnClick = { requestLogin = false },
                confirmOnClick = {
                    requestLogin = false
                    context.startActivity(Intent(context, StartActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            )
        }

        if(requestLogOut){
            LogOutToastMessage(
                dismissOnClick = { requestLogOut = false },
                confirmOnClick = {
                    viewModel.logOut()
                    navController.navigate(Screen.Home.route){
                        launchSingleTop = true
                    }
                    requestLogOut = false
                }
            )
        }
    }
}


@Composable
fun SettingRoot(
    modifier: Modifier = Modifier,
    userInfo: DomainUser,
    actionBtn: @Composable () -> Unit,
    requestLogin: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(text = "Settings", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 15.dp, top = 20.dp))
        menuItem(
            icon = R.drawable.setting_profile,
            title = "프로필 수정",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, ProfileEditActivity::class.java).putExtra("userInfo", Gson().toJson(userInfo)))
                }
            }
        )
        menuItem(
            icon = R.drawable.alert,
            title = "앱 관리", onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, AppVersionActivity::class.java))
                }
            }
        )
        menuItem(
            icon = R.drawable.notification,
            title = "공지사항",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, AnnounceActivity::class.java))
                }
            })
        menuItem(
            icon = R.drawable.help,
            title = "문의하기",
            onClick = {})
        menuItem(
            icon = R.drawable.alert,
            title = "이벤트",
            onClick = {})


        Spacer(modifier = Modifier.weight(1f))
        actionBtn()
    }
}

@Composable
fun menuItem(icon: Int, title: String, onClick: () -> Unit){

    Column(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 25.dp, bottom = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = null)
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = title, fontSize = 16.sp, color = Color.Black)
        }
        Divider(thickness = 0.5.dp, color = colorResource(id = R.color.lightgray))
    }
}

@Composable
fun actionButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, bottom = 20.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black
        ),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(vertical = 15.dp),
        onClick = {
            onClick()
        },
    ) { Text(text, fontSize = 16.sp, color = Color.White) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopAppBar(navController: NavController){
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "설정", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
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



@Preview(showSystemUi = true)
@Composable
fun TestSettingScreen(){
    var navController = rememberNavController()
    Scaffold(
        topBar = { SettingTopAppBar(navController) },
    ) { innerpadding ->
        SettingRoot(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            userInfo = DomainUser(),
            actionBtn = {
                actionButton ("로그인") { }
            },
            requestLogin = {}
        )
    }
}