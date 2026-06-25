package com.kipucode.domain.repository

import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    // Obtiene los ejercicios de forma reactiva desde Room (Offline-First)
    fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseDomain>>

    // Descarga los ejercicios de Firestore para una lección y los almacena en Room
    suspend fun refreshExercises(lessonId: String): Response<Unit>
}