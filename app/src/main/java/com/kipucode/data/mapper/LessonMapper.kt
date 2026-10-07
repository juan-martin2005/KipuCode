package com.kipucode.data.mapper

import com.kipucode.data.local.model.LessonEntity
import com.kipucode.domain.model.LessonDomain

import com.kipucode.data.local.converter.LessonBlockConverter

// ===================================
//  Room (Entity) -> Dominio
// ===================================
fun LessonEntity.toDomain() =
    LessonDomain(
        id = id,
        courseId = courseId,
        title = title,
        content = content,
        blocks = LessonBlockConverter.parse(content),
        xp = xp,
        orderIndex = orderIndex
    )