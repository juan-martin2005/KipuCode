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

// Forzar sincronización desde la nube (Firestore)
class RefreshExercisesUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(courseId: String, lessonId: String): Response<Unit> {
        return exerciseRepository.refreshExercises(courseId, lessonId)
    }
}