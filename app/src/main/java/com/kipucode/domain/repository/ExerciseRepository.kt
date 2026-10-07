package com.kipucode.domain.repository

import com.kipucode.domain.model.ExerciseDomain
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    // Obtiene los ejercicios de forma reactiva desde Room (Offline-First)
    fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseDomain>>

    fun getAllExercises(): Flow<List<ExerciseDomain>>
}