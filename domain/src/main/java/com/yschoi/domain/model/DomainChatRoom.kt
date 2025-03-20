package com.yschoi.domain.model

data class DomainChatRoom(
    var roomId: String = "",
    var artworkId: String = "",
    var users: Map<String, Boolean> = HashMap(), //채팅방 유저
    var status: Map<String, Boolean> = HashMap(), //채팅방 접속여부
    var lastMessage: DomainMessage = DomainMessage(),
    var unreadMessages: Map<String, Int> = HashMap(),
    var createdAt: String = "",
)