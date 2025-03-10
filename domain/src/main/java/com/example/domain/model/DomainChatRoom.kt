package com.example.domain.model

data class DomainChatRoom(
    var roomId: String = "",
    var artworkId: String = "",
    var users: Map<String, Boolean> = HashMap(),//채팅방 찾기 위한 용도
    var status: Map<String, Boolean> = HashMap(),//uid : 접속여부,
    var lastMessage: DomainMessage = DomainMessage(),
    var unreadMessages: Map<String, Int> = HashMap(),
    var createdAt: String = "",
)