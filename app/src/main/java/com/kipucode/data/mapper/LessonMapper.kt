package com.kipucode.data.mapper

import com.kipucode.data.local.model.LessonEntity
import com.kipucode.data.remote.firebase.dto.LessonDto
import com.kipucode.domain.model.Lesson

fun LessonDto.toEntity() = LessonEntity(
    id = id,
    courseId = courseId,
    title = title,
    content = content,
    points = points,
    exp = exp,
    orderIndex = orderIndex
)

fun LessonEntity.toDomain() = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    content = content,
    points = points,
    exp = exp,
    orderIndex = orderIndex
)