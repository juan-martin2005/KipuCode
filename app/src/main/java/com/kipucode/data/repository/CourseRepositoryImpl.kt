package com.kipucode.data.repository

import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.CourseRemoteDataSource
import com.kipucode.data.remote.firebase.service.LessonRemoteDataSource
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ErrorType
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
    private val courseRemoteDataSource: CourseRemoteDataSource,
    private val lessonRemoteDataSource: LessonRemoteDataSource,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao
): CourseRepository {

    // ===========================================================================================
    //  Obtiene los cursos locales (Entity) y los transforma al modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourses(): Flow<List<CourseDomain>> = courseDao.getAllCourses().map { list -> list.map { it.toDomain() } }

    // ===========================================================================================
    //  Observa un curso local (Entity) convirtiendo el resultado a modelo de Dominio (Domain)
    // ===========================================================================================
    override fun getCourseById(courseId: String): Flow<CourseDomain?> = courseDao.getCourseByIdFlow(courseId).map { courseEntity -> courseEntity?.toDomain()}


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
            val remoteCourses = courseRemoteDataSource.getCourses()
            courseDao.insertCourses(remoteCourses.map { it.toEntity() })

            for (course in remoteCourses) {
                val remoteLessons = lessonRemoteDataSource.getLessonByCourseId(course.id)
                lessonDao.insertAll(remoteLessons.map { it.toEntity() })
            }

            Response.Success(Unit)
        } catch (ex: Exception){
            Response.Error("Error syncing courses: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }
}