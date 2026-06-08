package com.kipucode.data.repository

import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao
): LessonRepository {
    // ===========================================================================================
    //  Flujo reactivo filtrado que obtiene lecciones (Entity) mapeadas a Dominio
    // ===========================================================================================
    override fun getLessonsByCourseId(courseId: String): Flow<List<LessonDomain>> =
        lessonDao.getLessonsByCourseId(courseId).map {  list -> list.map { it.toDomain() } }

    // ===========================================================================================
    //  Observa una lección local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getLessonById(lessonId: String): Flow<LessonDomain?> =
        lessonDao.getLessonById(lessonId).map { lessonEntity -> lessonEntity?.toDomain()}

}