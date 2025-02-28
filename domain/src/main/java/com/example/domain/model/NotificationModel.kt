package com.example.domain.model

data class NotificationModel(
    val token: String,
    val notification: Notification
)


data class Notification(
    val body: String,
    val title: String
)