package com.kipucode.data.mapper

import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.domain.model.User

fun UserDto.toDomain(): User{
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
    )
}

fun UserDto.toEntity(): UserEntity{
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
    )
}

fun UserEntity.toDomain() : User{
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
    )
}