package com.kipucode.domain.repository

import com.kipucode.domain.model.Course
import com.kipucode.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    suspend fun getCourses(): Response<List<Course>>
}