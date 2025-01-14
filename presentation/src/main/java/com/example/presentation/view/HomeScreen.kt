package com.example.presentation.view

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.presentation.R
import com.example.presentation.model.Screen
import com.example.presentation.utils.ArtInfo
import com.example.presentation.utils.CustomDialog
import com.example.presentation.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val isLoggedIn by viewModel.isLoggedInState
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pagerState = rememberPagerState(pageCount = { 3 })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // 메뉴의 초기 상태를 '닫힘'으로 설정
    val scope = rememberCoroutineScope() // 코루틴을 통해 메뉴 열림/닫힘을 제어
    var context = LocalContext.current


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(scope, drawerState, navController, viewModel, isLoggedIn, context)
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            HomeTopBar(scrollBehavior = scrollBehavior, isLoggedIn = isLoggedIn, onNavigationOnClick = { scope.launch { drawerState.open() }})
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sowoon_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
//                    .align(Alignment.Center)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.Black else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(12.dp)
                    )

                }
            }
            Spacer(modifier = Modifier.height(200.dp))
            val alpha = calculateAlpha(scrollState.value)
            ArtInfo(alpha)
        }
    }
}

// Alpha 계산 로직
fun calculateAlpha(scrollValue: Int): Float {
    Log.d("alpha", scrollValue.toString())
    val triggerStart = 300f  // Alpha 변화 시작 지점 (스크롤 값)
    val triggerEnd = 500f    // Alpha 변화 종료 지점 (스크롤 값)
    return ((scrollValue - triggerStart) / (triggerEnd - triggerStart)).coerceIn(0f, 1f)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(scrollBehavior: TopAppBarScrollBehavior, isLoggedIn: Boolean, onNavigationOnClick: () -> Unit){
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text(text = "Sowoon", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = { IconButton(onClick = { onNavigationOnClick() }) {
                Icon(Icons.Filled.List, contentDescription = null, Modifier.size(30.dp))
            }
        },
        actions = {
            if(!isLoggedIn){
                IconButton(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { context.startActivity(
                        Intent(context, StartActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY))}
                ){
                    Icon(Icons.Filled.Person, contentDescription = null, Modifier.size(30.dp))
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController,
    viewModel: MainViewModel,
    isLoggedIn: Boolean,
    context: Context,
) {
    val current = navController.currentBackStackEntry?.destination?.route
    var loginDialog by remember { mutableStateOf(false) }

    ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.48f)) {
        Text("Sowoon", modifier = Modifier.padding(16.dp), fontSize = 18.sp)
        Divider(thickness = 0.5.dp, color = Color.LightGray)
        NavigationDrawerItem(
            icon = {
                Icon(Icons.Outlined.Home, contentDescription = "홈")
            },
            label = { Text(text = "Home") },
            selected = current == Screen.Home.route,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Home.route)
            }
        )

        //Gallery 버튼
        NavigationDrawerItem(
            icon = {
                Icon(Icons.Outlined.List, contentDescription = "홈")
            },
            label = { Text(text = "Gallery") },
            selected = current == Screen.Profile.route,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Profile.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )


        //Favorites 버튼
        NavigationDrawerItem(
            icon = {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "홈")
            },
            label = { Text(text = "Favorites") },
            selected = current == Screen.Favorite.route,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Favorite.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )


        //Bookmark 버튼
        NavigationDrawerItem(
            icon = {
                Icon(painterResource(id = R.drawable.bookmark_border), contentDescription = "홈")
            },
            label = { Text(text = "Bookmark") },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.BookMark.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Setting 버튼
        NavigationDrawerItem(
            icon = {
                Icon(painterResource(id = R.drawable.setting_border), contentDescription = "설정")
            },
            label = { Text(text = "Setting") },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Setting.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        Divider(thickness = 0.5.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.weight(1f))

        //로그인, 로그아웃 버튼
        NavigationDrawerItem(
            label = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    scope.launch { drawerState.close() }
                    if (isLoggedIn) {
                        outLinedButton(onClick = { loginDialog = true }, text = "로그아웃")
                        if (loginDialog) {
                            CustomDialog(
                                onClickConfirm = { viewModel.logOut() },
                                { loginDialog = false },
                                "로그아웃"
                            )
                        }
                    } else {
                        outLinedButton("로그인") {
                            context.startActivity(
                                Intent(
                                    context,
                                    StartActivity::class.java
                                )
                            )
                        }
                    }
                }
            },
            selected = false,
            onClick = { }
        )

        // 회원탈퇴 버튼
        NavigationDrawerItem(
            label = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    outLinedButton("회원탈퇴") {
                        viewModel.deleteAccount()
                    }
                }
            },
            selected = false,
            onClick = { }
        )
    }
}

@Composable
fun outLinedButton(text: String, onClick: () -> Unit){
    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ){
        Text(text = text)
    }
}
@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun test(){
    Row {
        OutlinedButton(onClick = {} ){
            Icon(Icons.Outlined.Lock, contentDescription = "홈")
            Text(text = "로그아웃")
        }
    }
}