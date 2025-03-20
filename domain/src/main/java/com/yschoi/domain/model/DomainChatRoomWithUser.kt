package com.yschoi.domain.model

data class DomainChatRoomWithUser(
    var destUser: DomainUser? = DomainUser(),
    var chatRoom: DomainChatRoom = DomainChatRoom(),
    var artwork: DomainArtwork = DomainArtwork()
)
