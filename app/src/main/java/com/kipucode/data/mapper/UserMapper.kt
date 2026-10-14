package com.kipucode.data.mapper

import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.domain.model.UserDomain

// =========================================================================================
//  Domain -> DTO
fun UserDomain.toDto(): UserDto =
    UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        avatarId = this.avatarId
    )
// =========================================================================================
//  DTO -> Domain
fun UserDto.toDomain(): UserDomain =
    UserDomain(
        id = this.id,
        name = this.name,
        email = this.email,
        avatarId = this.avatarId
    )
// =========================================================================================
//  DTO -> Entity
fun UserDto.toEntity(): UserEntity =
    UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        avatarId = this.avatarId
    )
// =========================================================================================
//  Entity -> Domain
fun UserEntity.toDomain(): UserDomain =
    UserDomain(
        id = this.id,
        name = this.name,
        email = this.email,
        avatarId = this.avatarId
    )
