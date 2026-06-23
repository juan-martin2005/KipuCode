package com.kipucode.domain.usecase

import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetLessonByCourseUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
){
    operator fun invoke(lessonId: String): Flow<LessonDomain?> {
        return lessonRepository.getLessonById(lessonId)
    }
}