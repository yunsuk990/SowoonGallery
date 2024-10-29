package com.example.data.mapper

import com.example.data.model.DataUser
import com.example.domain.model.DomainUser

object MainMapper {

    fun userMapper(
        domainUser: DomainUser
    ): DataUser {
        return DataUser(
            name = domainUser.name,
            age = domainUser.age
        )
    }
}