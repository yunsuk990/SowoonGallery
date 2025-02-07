package com.example.presentation.view

import android.content.Intent
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
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun  SettingScreen(viewModel: MainViewModel, navController: NavHostController){

    val userInfo by viewModel.userInfoStateFlow.collectAsState()
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
                    context.startActivity(Intent(context, StartActivity::class.java))
                    requestLogin = false
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
            title = "프로필 수정",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, ProfileEditActivity::class.java).putExtra("userInfo", Gson().toJson(userInfo)))
                }
            }
        )
        menuItem("계정 관리"){}
        menuItem("앱 관리"){}
        menuItem("공지사항"){}
        menuItem("문의하기"){}
        menuItem("이벤트"){}
        Spacer(modifier = Modifier.weight(1f))
        actionBtn()
    }
}

@Composable
fun menuItem(title: String, onClick: () -> Unit){

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
            Icon(painter = painterResource(id = R.drawable.alert), contentDescription = null)
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
        topBar = { SettingTopAppBar(navController)},
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