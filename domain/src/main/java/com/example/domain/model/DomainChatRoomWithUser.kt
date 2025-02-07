package com.example.domain.model

data class DomainChatRoomWithUser(
    var destUser: DomainUser,
    var chatRoom: DomainChatRoom,
    var artwork: DomainArtwork
)
