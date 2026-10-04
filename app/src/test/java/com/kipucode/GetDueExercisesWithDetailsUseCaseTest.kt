package com.kipucode

import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.ExerciseRepository
import com.kipucode.domain.repository.LearningProgressRepository
import com.kipucode.domain.usecase.GetDueExercisesWithDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetDueExercisesWithDetailsUseCaseTest {

    private val fakeLearningProgressRepository = object : LearningProgressRepository {
        override fun getDueExercises(): Flow<List<LearningProgressDomain>> = flowOf(
            listOf(
                LearningProgressDomain(
                    exerciseId = "ex_01",
                    userId = "user_123",
                    stability = 3.5,
                    dueDate = 1000L,
                    reps = 2
                )
            )
        )
        override fun getProgressForExercise(exerciseId: String): Flow<LearningProgressDomain?> = flowOf(null)
        override fun getProgressForLesson(lessonId: String): Flow<List<LearningProgressDomain>> = flowOf(emptyList())
        override fun getProgressForCourse(courseId: String): Flow<List<LearningProgressDomain>> = flowOf(emptyList())
        override fun getAllProgressForCurrentUser(): Flow<List<LearningProgressDomain>> = flowOf(emptyList())
        override fun calculateCardRetrievability(progress: LearningProgressDomain): Double = 0.85
        override suspend fun recordChoiceAttempt(exerciseId: String, isCorrect: Boolean): Response<Unit> = Response.Success(Unit)
        override suspend fun recordRatingAttempt(exerciseId: String, ratingValue: Int): Response<Unit> = Response.Success(Unit)
        override suspend fun saveLearningProgress(progress: LearningProgressDomain): Response<Unit> = Response.Success(Unit)
        override suspend fun refreshLearningProgress(): Response<Unit> = Response.Success(Unit)
    }

    private val fakeExerciseRepository = object : ExerciseRepository {
        override fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseDomain>> = flowOf(emptyList())
        override fun getAllExercises(): Flow<List<ExerciseDomain>> = flowOf(
            listOf(
                ExerciseDomain(
                    id = "ex_01",
                    lessonId = "lesson_01",
                    type = "FLASHCARD",
                    instruction = "Pregunta de prueba",
                    orderIndex = 1
                )
            )
        )
        override suspend fun refreshExercises(lessonId: String): Response<Unit> = Response.Success(Unit)
    }

    private val fakeCourseRepository = object : CourseRepository {
        override fun getCourses(): Flow<List<CourseDomain>> = flowOf(emptyList())
        override fun getCourseById(courseId: String): Flow<CourseDomain?> = flowOf(null)
        override fun getCourseWithLessons(): Flow<List<CourseWithLessonsDomain>> = flowOf(
            listOf(
                CourseWithLessonsDomain(
                    course = CourseDomain(id = "c_01", title = "Java Básico"),
                    lessons = listOf(
                        LessonDomain(
                            id = "lesson_01",
                            courseId = "c_01",
                            title = "Introducción a Java",
                            orderIndex = 1
                        )
                    )
                )
            )
        )
        override suspend fun refreshCoursesAndLessons(): Response<Unit> = Response.Success(Unit)
    }

    @Test
    fun `cuando hay un ejercicio vencido, mapea correctamente el titulo de la leccion, tipo, estabilidad y porcentaje de retencion`() = runBlocking {
        val useCase = GetDueExercisesWithDetailsUseCase(
            learningProgressRepository = fakeLearningProgressRepository,
            exerciseRepository = fakeExerciseRepository,
            courseRepository = fakeCourseRepository
        )

        val result = useCase().first()

        assertEquals(1, result.size)
        val item = result[0]
        assertEquals("ex_01", item.exerciseId)
        assertEquals("lesson_01", item.lessonId)
        assertEquals("Introducción a Java", item.lessonTitle)
        assertEquals("c_01", item.courseId)
        assertEquals("Java Básico", item.courseTitle)
        assertEquals("FLASHCARD", item.exerciseType)
        assertEquals(85, item.retentionPercentage)
        assertEquals(3.5, item.stability, 0.001)
    }

    @Test
    fun `cuando la lista de vencidos esta vacia, retorna lista vacia`() = runBlocking {
        val emptyProgressRepo = object : LearningProgressRepository by fakeLearningProgressRepository {
            override fun getDueExercises(): Flow<List<LearningProgressDomain>> = flowOf(emptyList())
        }

        val useCase = GetDueExercisesWithDetailsUseCase(
            learningProgressRepository = emptyProgressRepo,
            exerciseRepository = fakeExerciseRepository,
            courseRepository = fakeCourseRepository
        )

        val result = useCase().first()
        assertTrue(result.isEmpty())
    }
}
