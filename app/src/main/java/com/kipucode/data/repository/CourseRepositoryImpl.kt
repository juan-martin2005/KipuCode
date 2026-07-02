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
    //  Sincronización Remota a Firebase
    // ===========================================================================================
    override suspend fun refreshCoursesAndLessons(): Response<Unit> = coroutineScope {
        try {
            // Descargar y guardar Cursos
            val remoteCourses = courseRemoteDataSource.getCourses()
            courseDao.insertCourses(remoteCourses.map { it.toEntity() })

            // Descargar Lecciones y Ejercicios en paralelo delegando la responsabilidad
            remoteCourses.map { course ->
                async { syncLessonsAndExercisesForCourse(course.id) }
            }.awaitAll()

            Response.Success(Unit)
        } catch (ex: Exception){
            Response.Error("Error syncing courses and exercises: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }

    // ===========================================================================================
    //  Sincronización 'functions'
    // ===========================================================================================
    private suspend fun syncLessonsAndExercisesForCourse(courseId: String) = coroutineScope {
        val remoteLessons = lessonRemoteDataSource.getLessonByCourseId(courseId)
        lessonDao.insertAll(remoteLessons.map { it.toEntity() })

        // Ejecutar la sincronización de ejercicios de forma concurrente por cada lección
        remoteLessons.map { lesson ->
            async { syncExercisesForLesson(lesson.id) }
        }.awaitAll()
    }

    private suspend fun syncExercisesForLesson(lessonId: String) {
        val remoteExercises = exerciseRemoteDataSource.getExercisesByLessonId(lessonId)

        // Limpiar ejercicios antiguos y guardar los nuevos de la lección
        exerciseDao.deleteExercisesByLessonId(lessonId)
        exerciseDao.insertAll(remoteExercises.map { it.toEntity() })

        // Extraer y agrupar todas las opciones (BlockOptions) en una sola lista para un insert masivo
        val allBlockOptions = remoteExercises.flatMap { exerciseDto ->
            exerciseDto.options.map { optionDto ->
                optionDto.toEntity(exerciseId = exerciseDto.id)
            }
        }

        if (allBlockOptions.isNotEmpty()) {
            blockOptionDao.insertAll(allBlockOptions)
        }
    }
}