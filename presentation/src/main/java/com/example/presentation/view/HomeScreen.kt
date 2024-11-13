package com.example.presentation.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.IconButton
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.logOutButton
import com.example.presentation.viewModel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedInState
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val pagerState = rememberPagerState(pageCount = { 3 })
    val drawerState = rememberDrawerState(DrawerValue.Closed) // 메뉴의 초기 상태를 '닫힘'으로 설정
    val scope = rememberCoroutineScope() // 코루틴을 통해 메뉴 열림/닫힘을 제어

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent ()
        }
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
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

            logOutButton(viewModel = viewModel)
        }
    }
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

@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Menu Item 1")
        Text(text = "Menu Item 2")
        Text(text = "Menu Item 3")
    }
}