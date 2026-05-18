package com.dam.kipucode.data.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jg.kipucode.data.local.model.Course
import com.jg.kipucode.data.local.model.Lesson

data class CourseWithLessons(
    @Embedded
    val course: Course,
    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )
    val lessons: List<Lesson>
)
