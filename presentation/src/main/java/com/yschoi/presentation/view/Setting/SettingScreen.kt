package com.yschoi.presentation.view.Setting

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yschoi.domain.model.DomainUser
import com.yschoi.presentation.R
import com.yschoi.presentation.model.Screen
import com.yschoi.presentation.utils.AutoResizedText
import com.yschoi.presentation.utils.LogOutToastMessage
import com.yschoi.presentation.utils.LoginToastMessage
import com.yschoi.presentation.view.StartActivity
import com.yschoi.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun SettingScreen(viewModel: MainViewModel, navController: NavHostController){

    val loggedInState by viewModel.isLoggedInState.collectAsState()
    val userInfo by viewModel.userInfoStateFlow.collectAsState()

    val context = LocalContext.current
    var dialogState by remember { mutableStateOf(false) }

    val loginActivityLauncher = rememberLauncherForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }

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
            requestLogin = { dialogState = true },
            actionBtn = {
                if(!loggedInState){
                    actionButton(text = "로그인", onClick = {
                        loginActivityLauncher.launch(
                            Intent(context, StartActivity::class.java).setFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY
                            )
                        )
                    })
                }else{
                    actionButton(text = "로그아웃", onClick = {
                        dialogState = true
                    })
                }
            }
        )

        if(dialogState){
            if(loggedInState){
                LogOutToastMessage(
                    dismissOnClick = { dialogState = false },
                    confirmOnClick = {
                        viewModel.logOut()
                        navController.navigate(Screen.Home.route){
                            launchSingleTop = true
                        }
                        dialogState = false
                    }
                )
            }else{
                LoginToastMessage(
                    dismissOnClick = { dialogState = false },
                    confirmOnClick = {
                        dialogState = false
                        loginActivityLauncher.launch(
                            Intent(context, StartActivity::class.java).setFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY
                            )
                        )
                    }
                )
            }
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
        Text(text = "Settings", color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(start = 15.dp, top = 20.dp, bottom = 15.dp))
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
            icon = R.drawable.lock,
            title = "계정 관리",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, AccountActivity::class.java).putExtra("userInfo", Gson().toJson(userInfo)))
                }
            }
        )
        menuItem(
            icon = R.drawable.release,
            title = "공지사항",
            onClick = {
                context.startActivity(Intent(context, AnnounceActivity::class.java))
            })
        menuItem(
            icon = R.drawable.notification,
            title = "알림 설정",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, NotificationActivity::class.java))
                }
            }
        )
        menuItem(
            icon = R.drawable.help,
            title = "문의하기",
            onClick = {
                if(userInfo.uid.isEmpty()){
                    requestLogin()
                }else{
                    context.startActivity(Intent(context, QuestionActivity::class.java).putExtra("userInfo", Gson().toJson(userInfo)))
                }
            })
        menuItem(
            icon = R.drawable.alert,
            title = "앱 관리", onClick = {
                context.startActivity(Intent(context, AppVersionActivity::class.java))
            }
        )
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
            Icon(painter = painterResource(id = icon), modifier = Modifier.size(28.dp) , contentDescription = null)
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
    ) { AutoResizedText(
        text = text, style = TextStyle(
            fontSize = 14.sp,
        ), color = Color.White,
        modifier = Modifier
    )}
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