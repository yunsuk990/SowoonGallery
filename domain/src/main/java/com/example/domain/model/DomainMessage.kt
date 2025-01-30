package com.example.domain.model

data class DomainMessage(
    var message: String = "",
    var senderUid: String = "",
    var timestamp: String = "",
    var readUsers: MutableMap<String, Any> = HashMap()
)
