package com.kipucode.data.repository

import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.CourseRemoteDataSource
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO COURSE_REPOSITORY
// ===============================================================================================
internal class CourseRepositoryImpl @Inject constructor(
    //  Instancia de CourseRemoteDataSource -> Acceso a datos remotos en Firestore
    //  Instancia de CourseDao -> Acceso a operaciones locales de Cursos (Room)
    //  Instancia de LessonDao -> Acceso a operaciones locales de Lecciones (Room)
    private val remoteDataSource: CourseRemoteDataSource,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao
): CourseRepository {
    //  ! IMPORTANTE
    //  Offline-First: Los flujos 'get' consumen directamente de Room (DB Local). La UI nunca
    //     experimenta retrasos de red al consultar datos ya almacenados.

    //  Mappers: Se usa '.map { ... }' de Flow para convertir las entidades de la base de datos
    //     (Entity) a modelos de negocio (Domain).

    //  SINCRONIZACIÓN: El 'refresh' descarga primero la estructura de cursos para traer las
    //     lecciones de cada uno desde Firestore.


    // ===========================================================================================
    //  Obtiene los cursos locales (Entity) y los transforma al modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourses(): Flow<List<CourseDomain>> =
        courseDao.getAllCourses().map { list -> list.map { it.toDomain() } }

    // ===========================================================================================
    //  Flujo reactivo filtrado que obtiene lecciones (Entity) mapeadas a Dominio
    // ===========================================================================================
    override fun getLessonsByCourseId(courseId: String): Flow<List<LessonDomain>> =
        courseDao.getLessonsByCourseId(courseId).map {  list -> list.map { it.toDomain() } }

    // ===========================================================================================
    //  Observa un curso local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourseById(courseId: String): Flow<CourseDomain?> =
        courseDao.getCourseByIdFlow(courseId).map { courseEntity -> courseEntity?.toDomain()}

    // ===========================================================================================
    //  Observa una lección local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getLessonById(lessonId: String): Flow<LessonDomain?> =
        lessonDao.getLessonByIdFlow(lessonId).map { lessonEntity -> lessonEntity?.toDomain()}


    override fun getCourseWithLessons(): Flow<List<CourseWithLessonsDomain>> =
        courseDao.getCourseWithLessons().map { list ->
            list.map { item ->
                CourseWithLessonsDomain(
                    course = item.course.toDomain(),
                    lessons = item.lessons.map { lesson -> lesson.toDomain() }
                )
            }
        }

    // ===========================================================================================
    //  Sincronización Remota -> Descarga y actualiza toda la estructura desde Firestore a Room
    // ===========================================================================================
    override suspend fun refreshCoursesAndLessons(): Response<Unit> {
        return try {
            val remoteCourses = remoteDataSource.getCourses()
            courseDao.insertCourses(remoteCourses.map { it.toEntity() })

            for (course in remoteCourses) {
                val remoteLessons = remoteDataSource.getLessonByCourseId(course.id)
                lessonDao.insertAll(remoteLessons.map { it.toEntity() })
            }

            Response.Success(Unit)
        } catch (ex: Exception){
            Response.Error("Error syncing courses: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }
}