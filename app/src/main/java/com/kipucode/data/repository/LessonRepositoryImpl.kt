package com.kipucode.data.repository

import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao
): LessonRepository {
    private val memoryCache = ConcurrentHashMap<String, LessonDomain>()

    // ===========================================================================================
    //  Flujo reactivo filtrado que obtiene lecciones (Entity) mapeadas a Dominio y puebla caché
    // ===========================================================================================
    override fun getLessonsByCourseId(courseId: String): Flow<List<LessonDomain>> =
        lessonDao.getLessonsByCourseId(courseId).map { list ->
            list.map { entity ->
                val domain = entity.toDomain()
                memoryCache[domain.id] = domain
                domain
            }
        }

    // ===========================================================================================
    //  Observa una lección local con caché en RAM (0ms) y sincronización reactiva desde Room
    // ===========================================================================================
    override fun getLessonById(lessonId: String): Flow<LessonDomain?> = flow {
        // Emitir inmediatamente desde la memoria RAM si ya fue consultada
        val cached = memoryCache[lessonId]
        if (cached != null) {
            emit(cached)
        }

        // Consultar / observar Room para asegurar datos actualizados
        lessonDao.getLessonById(lessonId).collect { lessonEntity ->
            val domain = lessonEntity?.toDomain()
            if (domain != null) {
                memoryCache[lessonId] = domain
            }
            emit(domain)
        }
    }
}