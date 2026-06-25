package com.kipucode.data.repository

import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.CourseRemoteDataSource
import com.kipucode.data.remote.firebase.service.ExerciseRemoteDataSource
import com.kipucode.data.remote.firebase.service.LessonRemoteDataSource
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    private val exerciseRemoteDataSource: ExerciseRemoteDataSource,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
    private val exerciseDao: ExerciseDao,
    private val blockOptionDao: BlockOptionDao
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
    override suspend fun refreshCoursesAndLessons(): Response<Unit> = coroutineScope {
        try {
            // Descargamos y guardamos todos los cursos desde Firebase a Room
            val remoteCourses = courseRemoteDataSource.getCourses()
            courseDao.insertCourses(remoteCourses.map { it.toEntity() })

            // Para cada curso descargamos sus lecciones en paralelo (ASYNC)
            val courseDeferredList = remoteCourses.map { course ->
                async {
                    val remoteLessons = lessonRemoteDataSource.getLessonByCourseId(course.id)
                    lessonDao.insertAll(remoteLessons.map { it.toEntity() })

                    // Para cada lección del curso, descargamos y guardamos los ejercicios
                    val lessonDeferredList = remoteLessons.map { lesson ->
                        async {
                            val remoteExercises = exerciseRemoteDataSource.getExercisesByLessonId(lesson.id)

                            exerciseDao.deleteExercisesByLessonId(lesson.id)

                            exerciseDao.insertAll(remoteExercises.map { it.toEntity() })

                            for (exerciseDto in remoteExercises) {
                                val blockOptionEntities = exerciseDto.options.map { optionDto ->
                                    optionDto.toEntity(exerciseId = exerciseDto.id)
                                }
                                blockOptionDao.insertAll(blockOptionEntities)
                            }
                        }
                    }
                    lessonDeferredList.awaitAll()
                }
            }
            courseDeferredList.awaitAll()

            Response.Success(Unit)
        } catch (ex: Exception){
            Response.Error("Error syncing courses and exercises: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }
}