package com.kipucode.domain.usecase

import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Obtener ejercicios locales
class GetExercisesByLessonUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(lessonId: String): Flow<List<ExerciseDomain>> {
        return exerciseRepository.getExercisesByLessonId(lessonId)
    }
}