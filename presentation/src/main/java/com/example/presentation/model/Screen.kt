package com.example.presentation.model

import com.example.presentation.R

sealed class Screen(val route: String, val title: String, val iconClicked: Int, val iconBorder: Int){
    object Home : Screen("home", "홈", R.drawable.home_filled, R.drawable.home_border)
    object Favorite : Screen("favorites", "좋아요",R.drawable.home_border, R.drawable.home_filled)
    object BookMark: Screen("bookmark", "북마크",R.drawable.home_border, R.drawable.home_filled)
    object Profile : Screen("profile", "작품",R.drawable.art_filled, R.drawable.art_border)
    object MyPage : Screen("my", "MY",R.drawable.person_filled, R.drawable.person_border)
    object Chat : Screen("chat", "채팅",R.drawable.chat_filled, R.drawable.chat_border)
    object Setting : Screen("setting", "설정",R.drawable.setting, R.drawable.setting_border)
}
