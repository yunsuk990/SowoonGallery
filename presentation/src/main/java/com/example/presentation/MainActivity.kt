package com.example.presentation


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SowoonTheme {
                val navController = rememberNavController()
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = { TopAppBar(scrollBehavior)},
                    bottomBar = { BottomAppBar(navController) },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) { innerPadding ->
                    MyAppNavHost(navController = navController, modifier = Modifier.padding(innerPadding) )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Test(){
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = { TopAppBar(scrollBehavior)},
        bottomBar = { BottomAppBar(navController) }
    ) { innerPadding ->
        MyAppNavHost(navController = navController, modifier = Modifier.padding(innerPadding) )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior){
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text(text = "Sowoon", textAlign = TextAlign.Center)},
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = { Column(modifier = Modifier.padding(start = 5.dp)) {
            Icon(Icons.Filled.List, contentDescription = null, Modifier.size(30.dp))
        } },
        actions = { Column(Modifier.padding(end = 5.dp)) {
            IconButton(
                onClick = { context.startActivity(Intent(context, StartActivity::class.java))}
            ){
                Icon(Icons.Filled.Person, contentDescription = null, Modifier.size(30.dp))
            }
        } },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun MyAppNavHost(navController: NavHostController, modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier){
        composable(Screen.Home.route) { HomeScreen(navController) }
//        composable(Screen.Favorites.route) { FavoritesScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController, "yunsuk") }
//        composable(
//            route = "details/{itemId}",
//            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
//            DetailsScreen(navController, itemId)
//        }
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