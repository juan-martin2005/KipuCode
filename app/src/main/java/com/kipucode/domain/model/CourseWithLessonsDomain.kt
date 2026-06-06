package com.kipucode.domain.model

data class CourseWithLessonsDomain(
    val course: CourseDomain,
    val lessons: List<LessonDomain>
)