package com.kipucode.data.local.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.LessonEntity

data class CourseWithLessons(
    @Embedded
    val course: CourseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )
    val lessons: List<LessonEntity>
)
