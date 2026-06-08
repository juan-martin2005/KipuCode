package com.kipucode.domain.repository

import com.kipucode.domain.model.LessonDomain
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun getLessonsByCourseId(courseId: String) : Flow<List<LessonDomain>>

    fun getLessonById(lessonId: String): Flow<LessonDomain?>

}