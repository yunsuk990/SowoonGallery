package com.example.domain.model

data class DomainChatRoom(
    var roomId: String = "",
    var artworkId: String = "",
    var users: Map<String, Boolean> = HashMap(),
    var lastMessage: DomainMessage = DomainMessage(),
    var unreadMessages: Map<String, Int> = HashMap(),
    var createdAt: String = ""
)