package com.kipucode.data.repository

import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao
): LessonRepository {

    // ===========================================================================================
    //  Observa una lección local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getLessonById(lessonId: String): Flow<LessonDomain?> =
        lessonDao.getLessonById(lessonId).map { lessonEntity -> lessonEntity?.toDomain() }
}