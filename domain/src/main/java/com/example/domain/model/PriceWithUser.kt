package com.example.domain.model

data class PriceWithUser(
    var uid: String = "",
    var name: String = "",
    var age: Int = 0,
    var price: Float = 0f,     // 가격
    var date: String = ""      // 날짜 (X축 키 값)
)
