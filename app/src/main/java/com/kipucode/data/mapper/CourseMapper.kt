package com.kipucode.data.mapper

import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.remote.firebase.dto.CourseDto
import com.kipucode.domain.model.CourseDomain

// ===================================
//  Room (Entity) -> Dominio
// ===================================
fun CourseEntity.toDomain(): CourseDomain =
    CourseDomain(
        id = id,
        title = title,
        description = description,
        track = track,
        orderIndex = orderIndex,
        points = points,
        exp = exp,
        createdAt = createdAt
    )

// ===================================
//  Domain -> Room (Entity)
// ===================================
fun CourseDto.toEntity(): CourseEntity =
    CourseEntity(
        id = id,
        title = title,
        description = description,
        track = track,
        orderIndex = orderIndex,
        points = points,
        exp = exp,
        createdAt = createdAt?.toDate()?.time
    )