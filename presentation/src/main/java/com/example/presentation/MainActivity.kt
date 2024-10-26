package com.example.presentation


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.model.Screen
import com.example.presentation.view.HomeScreen
import com.example.presentation.view.ProfileScreen
import com.example.presentation.view.StartActivity
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
        MyAppNavHost(navController = navController, modifier = Modifier.padding(innerPadding), viewModel )
    }


}

@Composable
fun MyAppNavHost(navController: NavHostController, modifier: Modifier = Modifier, viewModel: MainViewModel){
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier){
        composable(Screen.Home.route) { HomeScreen(viewModel) }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

@Composable
fun BottomAppBar(navController: NavHostController) {
    var items = listOf(Screen.Home, Screen.Profile)
    BottomNavigation(
        backgroundColor = Color.White
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        items.forEach{ screen ->
            BottomNavigationItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Gray,
                icon = { Icon(screen.icon, contentDescription = null) },
                //label = {Text(text =  screen.route )}
            )
        }
    }
}

@Composable
fun logOutButton(viewModel: MainViewModel){
    OutlinedButton(onClick = { viewModel.logOut()} ){
        Text(text = "로그아웃")
    }
}