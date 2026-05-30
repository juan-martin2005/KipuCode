package com.kipucode.data.mapper

import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.remote.firebase.dto.CourseDto
import com.kipucode.domain.model.Course

fun CourseDto.toEntity() = CourseEntity(
    id = id,
    title = title,
    description = description,
    orderIndex = orderIndex,
    completed = completed,
    createdAt = createdAt?.toDate()?.time
)

fun CourseEntity.toDomain() = Course(
    id = id,
    title = title,
    description = description,
    orderIndex = orderIndex,
    completed = completed,
    createdAt = createdAt
)