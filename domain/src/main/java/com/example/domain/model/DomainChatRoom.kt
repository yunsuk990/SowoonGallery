package com.example.domain.model

data class DomainChatRoom(
    var artworkId: String = "",
    var users: Map<String, Boolean> = HashMap(),
    var lastMessage: DomainMessage = DomainMessage(),
    var createdAt: String = ""
)