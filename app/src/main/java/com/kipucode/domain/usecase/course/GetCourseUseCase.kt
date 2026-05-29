package com.kipucode.domain.usecase.course

import com.kipucode.domain.model.Course
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository

class GetCourseUseCase(
    private val courseRepository: CourseRepository
){
    suspend operator fun invoke(): Response<List<Course>>{
        return courseRepository.getCourses()
    }
}