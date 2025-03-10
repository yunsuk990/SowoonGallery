package com.example.domain.model

data class NotificationModel(
    val token: String,
    val notification: Notification,
    val artwork: DomainArtwork = DomainArtwork(),
    val destUser: DomainUser = DomainUser()
)


data class Notification(
    val body: String,
    val title: String
)