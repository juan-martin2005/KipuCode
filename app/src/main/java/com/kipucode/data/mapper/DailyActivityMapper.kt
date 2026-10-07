package com.kipucode.data.mapper

import com.kipucode.data.local.model.DailyActivityEntity
import com.kipucode.data.remote.firebase.dto.DayActivityDto
import com.kipucode.domain.model.DailyActivityDomain

fun DailyActivityEntity.toDomain(): DailyActivityDomain =
    DailyActivityDomain(
        date = date,
        year = year,
        timestamp = timestamp,
        exercisesCount = exercisesCount,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        xpEarned = xpEarned,
        lessonsCount = lessonsCount
    )

fun DailyActivityDomain.toEntity(userId: String): DailyActivityEntity =
    DailyActivityEntity(
        id = "${userId}_$date",
        userId = userId,
        date = date,
        year = year,
        timestamp = if (timestamp > 0) timestamp else System.currentTimeMillis(),
        exercisesCount = exercisesCount,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        xpEarned = xpEarned,
        lessonsCount = lessonsCount
    )

fun DayActivityDto.toEntity(userId: String, date: String, year: Int): DailyActivityEntity =
    DailyActivityEntity(
        id = "${userId}_$date",
        userId = userId,
        date = date,
        year = year,
        exercisesCount = exercises,
        correctCount = correct,
        incorrectCount = incorrect,
        xpEarned = xp,
        lessonsCount = lessons
    )

fun DailyActivityDomain.toDto(): DayActivityDto =
    DayActivityDto(
        exercises = exercisesCount,
        correct = correctCount,
        incorrect = incorrectCount,
        xp = xpEarned,
        lessons = lessonsCount
    )
