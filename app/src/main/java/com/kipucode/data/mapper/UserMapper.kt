package com.kipucode.data.mapper

import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.domain.model.UserDomain

// ===================================
//  Dominio -> Firebase (DTO)
// ===================================
fun UserDomain.toDto(): UserDto =
    UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        )

// ===================================
//  Domain -> Room (Entity)
// ===================================
fun UserDomain.toEntity(): UserEntity =
    UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
    )

// ===================================
//  Firebase (DTO) -> Dominio
// ===================================
fun UserDto.toDomain(): UserDomain =
    UserDomain(
        id = this.id,
        name = this.name,
        email = this.email,
    )

// ===================================
//  Firebase (DTO) -> Room (Entity)
// ===================================
fun UserDto.toEntity(): UserEntity =
    UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
    )

// ===================================
//  Room (Entity) -> Dominio
// ===================================
fun UserEntity.toDomain() : UserDomain =
    UserDomain(
        id = this.id,
        name = this.name,
        email = this.email,
    )
