package com.example.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.presentation.R
import com.example.presentation.model.Screen
import com.example.presentation.viewModel.MainViewModel


@Composable
fun SettingScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
){
    var openProfileDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        profileUser(navController, viewModel) { openProfileDialog = true }
        Spacer(modifier = Modifier.height(30.dp))
        profileMenu()
    }
    if(openProfileDialog){
        ProfileEditDialog(viewModel) { openProfileDialog = false }
    }

}

@Composable
fun profileUser(
    navController: NavHostController,
    viewModel: MainViewModel,
    profileOnClick: () -> Unit,
) {

    val userInfo by viewModel.userInfoStateFlow.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .size(100.dp)
                .background(color = colorResource(id = R.color.lightgray))
                .clickable {
//                    navController.navigate(Screen.ProfileEdit.route) {
//                        launchSingleTop = true
//                    }
                    profileOnClick()
                },
            contentAlignment = Alignment.Center,
        ){
            if(userInfo.profileImage != null){
                AsyncImage(model = userInfo.profileImage, contentDescription = null, contentScale = ContentScale.Crop)
            }else{
                Icon(painter = painterResource(id = R.drawable.profile), contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
        Text(text = userInfo.name, letterSpacing = 1.sp ,fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(top = 25.dp))
        Row(
            modifier = Modifier.padding(top = 25.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            userSection(
                modifier = Modifier.weight(1f),
                "Liked",
                R.drawable.heart_border,
                userInfo.likedArtworks.size
            ) {
                navController.navigate(Screen.Favorite.route) {
                    launchSingleTop = true
                }
            }
            userSection(
                modifier = Modifier.weight(1f),
                "BookMark",
                R.drawable.bookmark_border,
                userInfo.favoriteArtworks.size
            ){
                navController.navigate(Screen.BookMark.route) {
                    launchSingleTop = true
                }
            }
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
            Icon(painter = painterResource(id = icon), contentDescription = "좋아요 작품", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 16.sp, color = Color.Black)
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun SettingScreenTest(){
    val navController = rememberNavController()
    Surface(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        Column(modifier = Modifier.background(Color.White)) {
        }
    }
}

@Composable
fun profileMenu(){
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
            0 -> settingScreen()
            1 -> differentArtworks()
        }
    }

}

@Composable
fun settingScreen() {
    Column(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
        menuItem()
        menuItem()
        menuItem()
        menuItem()
        menuItem()

    }
}

@Composable
fun menuItem(){

    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.alert), contentDescription = "앱 관리")
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = "앱 관리", fontSize = 16.sp, color = Color.Black)
        }
        Divider(thickness = 0.5.dp, color = colorResource(id = R.color.lightgray))
    }
}
