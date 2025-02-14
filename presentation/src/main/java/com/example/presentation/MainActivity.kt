package com.example.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.model.Screen
import com.example.presentation.view.*
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )
            SowoonTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel){
    val navController = rememberNavController()//
    val unreadMessage by viewModel.unreadMessageCount.collectAsState()

    Scaffold(
        bottomBar = { BottomAppBar(navController, unreadMessage) },
    ) { innerPadding ->
        MyAppNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.White),
            viewModel = viewModel,
        )
    }
}
@Composable
fun MyAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
){
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier){
        composable(Screen.Home.route) { HomeScreen(viewModel, navController) }
        composable(Screen.Profile.route) { ArtworkScreen(viewModel) }
        composable(Screen.Favorite.route) { LikedScreen(viewModel, navController)}
        composable(Screen.BookMark.route) { BookMarkScreen(viewModel, navController) }
        composable(Screen.MyPage.route) { MyPageScreen(viewModel, navController) }
        composable(Screen.Setting.route) { SettingScreen(viewModel, navController) }
        composable(Screen.Chat.route) { ChattingScreen(viewModel, navController) }
    }
}

@Composable
fun BottomAppBar(navController: NavHostController, unreadMessage: Int) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    var items = listOf(Screen.Home, Screen.Profile, Screen.Chat, Screen.MyPage)
    val currentRoute = backStackEntry?.destination?.route
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    // 현재 화면이 BottomNavigation에 포함된 화면인지 확인
    val currentScreenIndex = items.indexOfFirst { it.route == currentRoute }
    if (currentScreenIndex != -1) {
        selectedItemIndex = currentScreenIndex // 현재 화면이 포함된 경우 index 업데이트
    }


    BottomNavigation(
        backgroundColor = Color.White
    ) {
        items.forEachIndexed{ index, screen ->
            var isSelected = index == selectedItemIndex

            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    if(currentRoute != screen.route){
                        selectedItemIndex = index
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                selectedContentColor = Color.Gray,
                unselectedContentColor = Color.Gray,
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 12.sp,
                        color = if(isSelected){
                            Color.Black
                        }else{
                            Color.LightGray
                        },
                    )
                },
                icon = {
                    if(screen == Screen.Chat){
                        if(unreadMessage > 0){
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = colorResource(R.color.messageCount),
                                        contentColor = Color.White
                                    ){
                                        Text(unreadMessage.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    painter = if(isSelected) painterResource(id = items[index].iconClicked) else painterResource(id = items[index].iconBorder),
                                    tint = if(isSelected) Color.Black else Color.LightGray,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(35.dp)
                                        .padding(1.dp)
                                )
                            }
                        }else{
                            Icon(
                                painter = if(isSelected) painterResource(id = items[index].iconClicked) else painterResource(id = items[index].iconBorder),
                                tint = if(isSelected) Color.Black else Color.LightGray,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(1.dp)
                            )
                        }
                    }else{
                        Icon(
                            painter = if(isSelected) painterResource(id = items[index].iconClicked) else painterResource(id = items[index].iconBorder),
                            tint = if(isSelected) Color.Black else Color.LightGray,
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .padding(1.dp)
                        )
                    }
                },
                alwaysShowLabel = true,
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun mainActivityTest(){
}