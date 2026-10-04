package com.kipucode.data.repository

import com.kipucode.data.local.DatabaseSeedService
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.repository.CourseRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO COURSE_REPOSITORY
// ===============================================================================================
internal class CourseRepositoryImpl @Inject constructor(
    private val databaseSeedService: DatabaseSeedService,
    private val courseDao: CourseDao
): CourseRepository {

    // ===========================================================================================
    //  Obtiene los cursos locales (Entity) y los transforma al modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourses(): Flow<List<CourseDomain>> =
        courseDao.getAllCourses()
            .onStart { databaseSeedService.seedIfNeeded() }
            .map { list -> list.map { it.toDomain() } }

    // ===========================================================================================
    //  Observa un curso local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourseById(courseId: String): Flow<CourseDomain?> =
        courseDao.getCourseByIdFlow(courseId).map { courseEntity -> courseEntity?.toDomain() }


    override fun getCourseWithLessons(): Flow<List<CourseWithLessonsDomain>> =
        courseDao.getCourseWithLessons()
            .onStart { databaseSeedService.seedIfNeeded() }
            .map { list ->
                list.map { item ->
                    CourseWithLessonsDomain(
                        course = item.course.toDomain(),
                        lessons = item.lessons.sortedBy { it.orderIndex }.map { lesson -> lesson.toDomain() }
                    )
                }
            }

    // ===========================================================================================
    //  Carga Inicial / Sincronización Local desde Assets
    // ===========================================================================================
    override suspend fun refreshCoursesAndLessons(): Response<Unit> = coroutineScope {
        try {
            databaseSeedService.seedIfNeeded()
            Response.Success(Unit)
        } catch (ex: Exception) {
            Response.Error("Error al inicializar cursos locales: ${ex.message}", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}