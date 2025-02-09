package com.example.presentation.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.Screen
import com.example.presentation.utils.LoginToastMessage
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson


@Composable
fun MyPageScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
){
    val isLoggedInState by viewModel.isLoggedInState.collectAsState()
    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    var requestLogin by remember { mutableStateOf(false) }
    val context = LocalContext.current
    MyPageRoot(
        navController = navController,
        isLoggedInState = isLoggedInState,
        userInfo = userInfo,
        onLikedBtnClick = { navController.navigate(Screen.Favorite.route) { launchSingleTop = true }} ,
        onBookMarkBtnClick = { navController.navigate(Screen.BookMark.route) { launchSingleTop = true }} ,
        onProfileImageClick = {
            if(isLoggedInState){
                context.startActivity(Intent(context, ProfileEditActivity::class.java).putExtra("userInfo", Gson().toJson(userInfo)))
            }else{
                requestLogin = true
            }
        },
        logOutBtnOnClick = { viewModel.logOut() }
    )
    if(requestLogin){
        LoginToastMessage(
            dismissOnClick = { requestLogin = false},
            confirmOnClick = { context.startActivity(Intent(context, StartActivity::class.java))}
        )
    }

}

@Composable
fun MyPageRoot(
    isLoggedInState: Boolean,
    userInfo: DomainUser,
    onLikedBtnClick: () -> Unit,
    onBookMarkBtnClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    logOutBtnOnClick: () -> Unit,
    navController: NavHostController
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        profileUser(
            isLoggedInState = isLoggedInState,
            userInfo = userInfo,
            navController = navController,
            onLikedBtnClick = onLikedBtnClick,
            onBookMarkBtnClick = onBookMarkBtnClick,
            onProfileImageClick = onProfileImageClick
        )
        Spacer(modifier = Modifier.height(30.dp))
        profileMenu(userInfo, logOutBtnOnClick = logOutBtnOnClick)
    }
}

@Composable
fun profileUser(
    isLoggedInState: Boolean,
    userInfo: DomainUser,
    onLikedBtnClick: () -> Unit,
    onBookMarkBtnClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    navController: NavHostController
) {
    val modeText = when(userInfo.mode){
        0 -> "User"
        1 -> "Artist"
        2 -> "Manager"
        else -> "None"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center // 프로필 이미지를 중앙 정렬
        ) {
            // 프로필 이미지 박스
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .size(100.dp)
                    .background(color = colorResource(id = R.color.lightgray))
                    .clickable { onProfileImageClick() },
                contentAlignment = Alignment.Center,
            ) {
                if (userInfo.profileImage.isNotEmpty()) {
                    AsyncImage(
                        model = userInfo.profileImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 설정 아이콘 (우측 상단 정렬)
            Icon(
                painter = painterResource(R.drawable.setting),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd) // 오른쪽 끝 정렬
                    .padding(end = 16.dp) // 우측 여백 추가
                    .size(32.dp)
                    .noRippleClickable {
                        navController.navigate("setting") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            )
        }
        if(isLoggedInState){
            Box(
                modifier = Modifier
                    .padding(top = 15.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = colorResource(R.color.lightgray))
                    .padding(horizontal = 15.dp, vertical = 2.dp)
            ){
                Text(modeText, color=Color.Black, fontSize = 14.sp)
            }
            Text(
                text = userInfo.name,
                letterSpacing = 1.sp ,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 10.dp)
            )
        }else{
            Text(text = "로그인", textDecoration = TextDecoration.Underline, fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(top = 25.dp))
        }
        Row(
            modifier = Modifier.padding(top = 15.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            userSection(
                modifier = Modifier.weight(1f),
                "Liked",
                R.drawable.heart_border,
                userInfo.likedArtworks.size
            ) { onLikedBtnClick()}
            userSection(
                modifier = Modifier.weight(1f),
                "BookMark",
                R.drawable.bookmark_border,
                userInfo.favoriteArtworks.size
            ){ onBookMarkBtnClick() }
        }
    }
}

@Composable
fun userSection(modifier: Modifier, title: String, icon: Int, size: Int, onClick: () -> Unit){
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentSize()
            .clickable(indication = null, interactionSource = interactionSource) {
                onClick()
            }
    ){
        Text(text = size.toString(), fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 16.sp, color = Color.Black)
        }
    }
}


@Composable
fun profileMenu(userInfo: DomainUser, logOutBtnOnClick: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("설정", "구매한 작품")
    Column {
        TabRow(
            selectedTabIndex = selectedIndex,
            backgroundColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = Color.Black,
                    height = 1.dp
                )
            }
        ) {

            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            color = if(selectedIndex == index){
                                Color.Black
                            }else{
                                Color.LightGray
                            }
                        ) },
                    onClick = { selectedIndex = index}
                )
            }
        }
        when(selectedIndex){
            0 -> settingScreen(userInfo, logOutBtnOnClick = logOutBtnOnClick)
            1 -> differentArtworks()
        }
    }

}

@Composable
fun settingScreen(userInfo: DomainUser, logOutBtnOnClick: () -> Unit) {

}

@Composable
fun differentArtworks(){

}

@Preview(showSystemUi = true)
@Composable
fun myPageScreenPreview(){
    val navController = rememberNavController()
    Surface(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        Column(modifier = Modifier.background(Color.White)) {
            MyPageRoot(isLoggedInState = true, DomainUser(name = "YunSuk"), {}, {}, {}, {}, navController)

        }
    }
}
