package com.yschoi.presentation.model

import com.yschoi.presentation.R

sealed class Screen(val route: String, val title: String, val iconClicked: Int, val iconBorder: Int){
    object Home : Screen("home", "홈", R.drawable.home_filled, R.drawable.home_border)
    object Profile : Screen("profile", "작품",R.drawable.art_filled, R.drawable.art_border)
    object MyPage : Screen("my", "MY",R.drawable.person_filled, R.drawable.person_border)
    object Chat : Screen("chat", "채팅",R.drawable.chat_filled, R.drawable.chat_border)
    object Favorite : Screen("favorites", "좋아요",0, 0)
    object BookMark: Screen("bookmark", "북마크",0, 0)
    object Setting : Screen("setting", "설정",0, 0)
    object Banner : Screen("banner", "체험",0,0)
}
