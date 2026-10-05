package com.kipucode.data.mapper

import com.google.firebase.Timestamp
import com.kipucode.data.local.dao.relation.UserProgressWithDetails
import com.kipucode.data.local.model.UserCompletedCourseEntity
import com.kipucode.data.local.model.UserCompletedLessonEntity
import com.kipucode.data.local.model.UserProgressEntity
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import com.kipucode.domain.model.UserProgressDomain
import java.util.Date

// ===================================
//  Room (Entity / Relation) -> Dominio
// ===================================
fun UserProgressEntity.toDomain(): UserProgressDomain {
    return UserProgressDomain(
        id = this.userId,
        userId = this.userId,
        activeTrack = this.activeTrack,
        lastVisitedLessonId = this.lastVisitedLessonId,
        completedAt = this.completedAt,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedLessons = emptyList(),
        completedCourses = emptyList(),
        lessonsXpRecord = emptyMap()
    )
}

fun UserProgressWithDetails.toDomain(): UserProgressDomain {
    return UserProgressDomain(
        id = this.progress.userId,
        userId = this.progress.userId,
        activeTrack = this.progress.activeTrack,
        lastVisitedLessonId = this.progress.lastVisitedLessonId,
        completedAt = this.progress.completedAt,
        totalXp = this.progress.totalXp,
        streakDay = this.progress.streakDay,
        completedLessons = this.completedLessons.map { it.lessonId },
        completedCourses = this.completedCourses.map { it.courseId },
        lessonsXpRecord = this.completedLessons.associate { it.lessonId to it.xpEarned }
    )
}

// ===================================
//  Firebase (DTO) -> Room Entities
// ===================================
fun UserProgressDto.toEntity(): UserProgressEntity {
    return UserProgressEntity(
        userId = this.userId,
        activeTrack = this.activeTrack,
        lastVisitedLessonId = this.lastVisitedLessonId,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedAt = this.completedAt?.toDate()?.time
    )
}

fun UserProgressDto.toCompletedLessonsEntities(): List<UserCompletedLessonEntity> {
    val fallbackTime = this.completedAt?.toDate()?.time ?: System.currentTimeMillis()
    return this.completedLessons.map { lessonId ->
        UserCompletedLessonEntity(
            userId = this.userId,
            lessonId = lessonId,
            xpEarned = this.lessonsXpRecord[lessonId] ?: 0,
            completedAt = fallbackTime
        )
    }
}

fun UserProgressDto.toCompletedCoursesEntities(): List<UserCompletedCourseEntity> {
    val fallbackTime = this.completedAt?.toDate()?.time ?: System.currentTimeMillis()
    return this.completedCourses.map { courseId ->
        UserCompletedCourseEntity(
            userId = this.userId,
            courseId = courseId,
            completedAt = fallbackTime
        )
    }
}

// ===================================
//  Domain -> Firebase (DTO)
// ===================================
fun UserProgressDomain.toDto(): UserProgressDto {
    return UserProgressDto(
        userId = this.userId,
        activeTrack = this.activeTrack,
        lastVisitedLessonId = this.lastVisitedLessonId,
        completedAt = this.completedAt?.let { Timestamp(Date(it)) },
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedLessons = this.completedLessons,
        completedCourses = this.completedCourses,
        lessonsXpRecord = this.lessonsXpRecord
    )
}

// ===================================
//  Domain -> Room Entities
// ===================================
fun UserProgressDomain.toEntity(): UserProgressEntity {
    return UserProgressEntity(
        userId = this.userId,
        activeTrack = this.activeTrack,
        lastVisitedLessonId = this.lastVisitedLessonId,
        totalXp = this.totalXp,
        streakDay = this.streakDay,
        completedAt = this.completedAt
    )
}

fun UserProgressDomain.toCompletedLessonsEntities(): List<UserCompletedLessonEntity> {
    val time = this.completedAt ?: System.currentTimeMillis()
    return this.completedLessons.map { lessonId ->
        UserCompletedLessonEntity(
            userId = this.userId,
            lessonId = lessonId,
            xpEarned = this.lessonsXpRecord[lessonId] ?: 0,
            completedAt = time
        )
    }
}

fun UserProgressDomain.toCompletedCoursesEntities(): List<UserCompletedCourseEntity> {
    val time = this.completedAt ?: System.currentTimeMillis()
    return this.completedCourses.map { courseId ->
        UserCompletedCourseEntity(
            userId = this.userId,
            courseId = courseId,
            completedAt = time
        )
    }
}
