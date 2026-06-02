package com.kipucode.data.mapper

import com.kipucode.data.local.model.UserProgressEntity
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import com.kipucode.domain.model.UserProgress

fun UserProgressDto.toDomain(): UserProgress {
    return UserProgress(
        id = this.userId,
        userId = this.userId,
        currentLessonId = this.currentLessonId,
        status = "active", // Default status
        score = this.score,
        completedAt = null,
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDays,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses
    )
}

fun UserProgress.toEntity(): UserProgressEntity {
    return UserProgressEntity(
        id = this.id,
        userId = this.userId,
        lessonId = this.currentLessonId,
        status = this.status,
        score = this.score,
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDay,
        completedAt = this.completedAt
    )
}

fun UserProgressEntity.toDomain(): UserProgress {
    return UserProgress(
        id = this.id,
        userId = this.userId ?: "",
        currentLessonId = this.lessonId ?: "",
        status = this.status ?: "",
        score = this.score,
        completedAt = this.completedAt,
        totalXp = this.totalXp,
        points = this.points,
        streakDay = this.streakDay,
        completedLessons = emptyList(), // Room doesn't persist lists without TypeConverters
        completedCourses = emptyList()
    )
}
