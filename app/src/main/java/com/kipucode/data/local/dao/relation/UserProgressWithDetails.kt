package com.kipucode.data.local.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kipucode.data.local.model.UserCompletedCourseEntity
import com.kipucode.data.local.model.UserCompletedLessonEntity
import com.kipucode.data.local.model.UserProgressEntity

data class UserProgressWithDetails(
    @Embedded
    val progress: UserProgressEntity,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val completedLessons: List<UserCompletedLessonEntity> = emptyList(),

    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val completedCourses: List<UserCompletedCourseEntity> = emptyList()
)
