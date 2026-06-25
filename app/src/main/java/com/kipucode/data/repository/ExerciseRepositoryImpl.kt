package com.kipucode.data.repository

import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.ExerciseRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseRemoteDataSource: ExerciseRemoteDataSource,
    private val exerciseDao: ExerciseDao,
    private val blockOptionDao: BlockOptionDao
) : ExerciseRepository {

    // Obtiene de Room los ejercicios locales y los mapea al dominio
    override fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseDomain>> {
        return exerciseDao.getExercisesByLessonId(lessonId).map { relationsList ->
            relationsList.map { it.toDomain() }
        }
    }

    // Descarga desde Firestore y guarda en local (Room)
    override suspend fun refreshExercises(lessonId: String): Response<Unit> {
        return try {
            val remoteExercises = exerciseRemoteDataSource.getExercisesByLessonId(lessonId)

            exerciseDao.deleteExercisesByLessonId(lessonId)

            // Convertimos y guardamos los ejercicios
            exerciseDao.insertAll(remoteExercises.map { it.toEntity() })

            // Recorremos los ejercicios y guardamos las opciones
            for (exerciseDto in remoteExercises) {
                val blockOptionEntities = exerciseDto.options.map { optionDto ->
                    optionDto.toEntity(exerciseId = exerciseDto.id)
                }
                blockOptionDao.insertAll(blockOptionEntities)
            }

            Response.Success(Unit)
        } catch (ex: Exception) {
            Response.Error("Error al sincronizar: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }


}