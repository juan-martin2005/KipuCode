package com.kipucode.domain.repository

import com.kipucode.domain.model.LessonDomain
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun getLessonById(lessonId: String): Flow<LessonDomain?>

}