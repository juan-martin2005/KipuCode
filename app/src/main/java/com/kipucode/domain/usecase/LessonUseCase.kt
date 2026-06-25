package com.kipucode.domain.usecase

import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.LessonRepository
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class GetLessonByCourseUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
){
    operator fun invoke(lessonId: String): Flow<LessonDomain?> {
        return lessonRepository.getLessonById(lessonId)
    }
}

class CompleteLessonUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(completedLessonId: String, xpEarned: Int): Response<Unit> {
        return try {
            val coursesWithLessons = courseRepository.getCourseWithLessons().first()

            userProgressRepository.completeLesson(completedLessonId, xpEarned, coursesWithLessons)

        } catch (e: Exception) {
            Response.Error("${e.localizedMessage}", ErrorType.FIRESTORE_ERROR)
        }
    }
}