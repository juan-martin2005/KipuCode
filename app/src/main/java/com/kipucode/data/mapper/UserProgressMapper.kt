package com.kipucode.data.mapper

import com.google.firebase.Timestamp
import com.kipucode.data.local.model.UserProgressEntity
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import com.kipucode.domain.model.UserProgressDomain
import java.util.Date

// ===================================
//  Room (Entity) -> Dominio
// ===================================
fun UserProgressEntity.toDomain(): UserProgressDomain {
    return UserProgressDomain(
        id = this.id,
        userId = this.userId,
        currentLessonId = this.lessonId ?: "",
        status = this.status ?: "",
        completedAt = this.completedAt,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses,
        lessonsXpRecord = this.lessonsXpRecord
    )
}

// ===================================
//  Firebase (DTO) -> Room (Entity)
// ===================================
fun UserProgressDto.toEntity(): UserProgressEntity {
    return UserProgressEntity(
        id = this.userId,
        userId = this.userId,
        lessonId = this.currentLessonId,
        status = this.status,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedAt = this.completedAt?.toDate()?.time,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses,
        lessonsXpRecord = this.lessonsXpRecord
    )
}

fun UserProgressDomain.toDto(): UserProgressDto {
    return UserProgressDto(
        userId = this.userId,
        currentLessonId = this.currentLessonId,
        status = this.status,
        completedAt = this.completedAt?.let { Timestamp(Date(it)) },
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses,
        lessonsXpRecord = this.lessonsXpRecord
    )
}

fun UserProgressDomain.toEntity(): UserProgressEntity {
    return UserProgressEntity(
        id = this.id,
        userId = this.userId,
        lessonId = this.currentLessonId,
        status = this.status,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedAt = this.completedAt,
        completedCourses = this.completedCourses,
        completedLessons = this.completedLessons,
        lessonsXpRecord = this.lessonsXpRecord
    )
}

