package com.kipucode.data.mapper

import com.kipucode.data.local.model.CourseEntity
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
        xp = xp,
        createdAt = createdAt
    )