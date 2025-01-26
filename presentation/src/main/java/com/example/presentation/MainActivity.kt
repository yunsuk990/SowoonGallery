package com.example.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.model.Screen
import com.example.presentation.view.BookMarkScreen
import com.example.presentation.view.HomeScreen
import com.example.presentation.view.LikedScreen
import com.example.presentation.view.ProfileScreen
import com.example.presentation.view.SettingScreen
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.wait

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
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomAppBar(navController) },
    ) { innerPadding ->
        MyAppNavHost(navController = navController, modifier = Modifier
            .padding(innerPadding)
            .background(Color.White), viewModel )
    }


}

@Composable
fun MyAppNavHost(navController: NavHostController, modifier: Modifier = Modifier, viewModel: MainViewModel){
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier){
        composable(Screen.Home.route) { HomeScreen(viewModel, navController) }
        composable(Screen.Profile.route) { ProfileScreen(viewModel) }
        composable(Screen.Favorite.route) { LikedScreen(viewModel, navController)}
        composable(Screen.BookMark.route) { BookMarkScreen(viewModel, navController) }
        composable(Screen.Setting.route) { SettingScreen(viewModel, navController) }
    }
}

@Composable
fun BottomAppBar(navController: NavHostController) {
    var onItemSelected by remember { mutableStateOf(0) }
    var items = listOf(Screen.Home, Screen.Profile, Screen.Setting)

    BottomNavigation(
        backgroundColor = Color.White
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()

        items.forEachIndexed{ index, screen ->
            BottomNavigationItem(
                selected = onItemSelected == index,
                onClick = {
                    onItemSelected = index
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Gray,
                unselectedContentColor = Color.Gray,
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 12.sp,
                        color = if(onItemSelected == index){
                            Color.Black
                        }else{
                            Color.LightGray
                        },
                    )
                },
                icon = {
                    Icon(
                        painter = if(onItemSelected == index) painterResource(id = items[index].iconClicked) else painterResource(id = items[index].iconBorder),
                        tint = if(onItemSelected == index) Color.Black else Color.LightGray,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp).padding(1.dp)
                    )
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
    val navController = rememberNavController()
    var onItemSelected by remember { mutableStateOf(0) }
    var items = listOf(Screen.Home, Screen.Profile, Screen.Setting)
    BottomNavigation(
        backgroundColor = Color.White,
        modifier = Modifier.wrapContentSize().padding(20.dp)
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()

        items.forEachIndexed{ index, screen ->
            BottomNavigationItem(
                selected = onItemSelected == index,
                onClick = {
                    onItemSelected = index
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Gray,
                unselectedContentColor = Color.Gray,
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 10.sp,
                        color = if(onItemSelected == index){
                            Color.Black
                        }else{
                            Color.LightGray
                        },
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.person_border),
                        tint = if(onItemSelected == index) Color.Black else Color.LightGray,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).padding(1.dp)
                    )
                },
                alwaysShowLabel = true,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}