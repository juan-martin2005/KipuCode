package com.kipucode.domain.usecase.course

import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourseWithLessonsUseCase @Inject constructor(
    private val courseRepository: CourseRepository
){
    operator fun invoke(): Flow<List<CourseWithLessonsDomain>> {
        return courseRepository.getCourseWithLessons()
    }
}

class RefreshCoursesUseCase @Inject constructor(
    private val courseRepository: CourseRepository
){
    suspend operator fun invoke(): Response<Unit> {
        return courseRepository.refreshCoursesAndLessons()
    }
}

class GetAllCourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(): Flow<List<CourseDomain>> {
        return courseRepository.getCourses()
    }
}

class GetCourseByIdUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: String): Flow<CourseDomain?> {
        return courseRepository.getCourseById(courseId)
    }
}