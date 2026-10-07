package com.kipucode.data.repository

import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    // Obtiene de Room los ejercicios locales y los mapea al dominio
    override fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseDomain>> {
        return exerciseDao.getExercisesByLessonId(lessonId).map { relationsList ->
            relationsList.map { it.toDomain() }
        }
    }

    override fun getAllExercises(): Flow<List<ExerciseDomain>> {
        return exerciseDao.getAllExercises().map { relationsList ->
            relationsList.map { it.toDomain() }
        }
    }
}