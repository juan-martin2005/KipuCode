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
        userId = this.userId ?: "",
        currentLessonId = this.lessonId ?: "",
        status = this.status ?: "",
        score = this.score,
        completedAt = this.completedAt,
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDay,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses
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
        score = this.score,
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDay,
        completedAt = this.completedAt?.toDate()?.time,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses
    )
}

fun UserProgressDomain.toDto(): UserProgressDto {
    return UserProgressDto(
        userId = this.userId,
        currentLessonId = this.currentLessonId,
        status = this.status,
        score = this.score,
        completedAt = this.completedAt?.let { Timestamp(Date(it)) },
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDay,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses
    )
}

//fun UserProgressDomain.toEntity(): UserProgressEntity {
//    return UserProgressEntity(
//        id = this.id,
//        userId = this.userId,
//        lessonId = this.currentLessonId,
//        status = this.status,
//        score = this.score,
//        totalXp = this.totalXp,
//        points = this.points,
//        streakDay = this.streakDay,
//        completedAt = this.completedAt
//    )
//}
//
