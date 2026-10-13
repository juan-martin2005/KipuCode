package com.kipucode.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.DailyActivityDao
import com.kipucode.data.local.dao.ExerciseAttemptDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LearningProgressDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.DailyActivityEntity
import com.kipucode.data.local.model.ExerciseAttemptEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LearningProgressEntity
import com.kipucode.data.local.model.LessonEntity
import com.kipucode.data.local.model.UserCompletedCourseEntity
import com.kipucode.data.local.model.UserCompletedLessonEntity
import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.local.model.UserProgressEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas de integración sobre Room Database en memoria (PI-01 a PI-05).
 * Valida la persistencia relacional, integridad referencial de llaves foráneas,
 * colas de repaso FSRS offline y transacciones atómicas de progreso del usuario bajo el patrón AAA.
 */
@RunWith(AndroidJUnit4::class)
class RoomIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var courseDao: CourseDao
    private lateinit var lessonDao: LessonDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseAttemptDao: ExerciseAttemptDao
    private lateinit var dailyActivityDao: DailyActivityDao
    private lateinit var learningProgressDao: LearningProgressDao
    private lateinit var userProgressDao: UserProgressDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        courseDao = db.courseDao()
        lessonDao = db.lessonDao()
        exerciseDao = db.exerciseDao()
        exerciseAttemptDao = db.exerciseAttemptDao()
        dailyActivityDao = db.dailyActivityDao()
        learningProgressDao = db.learningProgressDao()
        userProgressDao = db.userProgressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // ============================================================================================
    //  PI-01: Persistencia Relacional de Cursos y Lecciones en Room
    // ============================================================================================

    /**
     * PI-01: Verifies 1:N relational persistence between courses and lessons in Room.
     */
    @Test
    fun verifyCourseAndLessonsRelationalPersistence() = runBlocking {
        // Arrange
        val course = CourseEntity(
            id = "course_csharp_intro",
            title = "C# desde Cero",
            description = "Fundamentos del lenguaje C#",
            track = "c_sharp",
            orderIndex = 1,
            xp = 100
        )
        val lesson1 = LessonEntity(
            id = "lesson_cs_01",
            courseId = "course_csharp_intro",
            title = "Variables y Tipos",
            content = "Introducción a tipos de datos",
            xp = 25,
            orderIndex = 1
        )
        val lesson2 = LessonEntity(
            id = "lesson_cs_02",
            courseId = "course_csharp_intro",
            title = "Estructuras de Control",
            content = "Condicionales y bucles",
            xp = 35,
            orderIndex = 2
        )

        // Act
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(listOf(lesson1, lesson2))
        val coursesWithLessons = courseDao.getCourseWithLessons().first()

        // Assert
        assertEquals(1, coursesWithLessons.size)
        val result = coursesWithLessons[0]
        assertEquals("course_csharp_intro", result.course.id)
        assertEquals(2, result.lessons.size)
        assertEquals("lesson_cs_01", result.lessons[0].id)
        assertEquals("lesson_cs_02", result.lessons[1].id)
    }

    /**
     * PI-01: Verifies filtering and counting courses by track.
     */
    @Test
    fun verifyCourseFilteringAndCountingByTrack() = runBlocking {
        // Arrange
        val courses = listOf(
            CourseEntity(id = "c_trk_1", title = "Basic Kotlin", description = "Fundamentals", track = "android", orderIndex = 1),
            CourseEntity(id = "c_trk_2", title = "Compose UI", description = "Modern UI", track = "android", orderIndex = 2),
            CourseEntity(id = "c_trk_3", title = "Node.js REST", description = "Backend API", track = "backend", orderIndex = 1)
        )

        // Act
        courseDao.insertCourses(courses)
        val androidCount = courseDao.getCoursesCountByTrack("android")
        val backendCount = courseDao.getCoursesCountByTrack("backend")
        val devopsCount = courseDao.getCoursesCountByTrack("devops")

        // Assert
        assertEquals(2, androidCount)
        assertEquals(1, backendCount)
        assertEquals(0, devopsCount)
    }

    // ============================================================================================
    //  PI-02: Registro de Intentos y Recuperación de Ejercicios
    // ============================================================================================

    /**
     * PI-02: Verifies recording and querying exercise attempt history with referential integrity.
     */
    @Test
    fun verifyExerciseAttemptsRecordAndQuery() = runBlocking {
        // Arrange
        val user = UserEntity(id = "user_001", name = "Estudiante UPN", email = "n001@upn.pe", avatarId = "avatar_001")
        val course = CourseEntity(id = "c1", title = "C# Básico", description = "Desc", track = "c_sharp", orderIndex = 1)
        val lesson = LessonEntity(id = "l1", courseId = "c1", title = "Intro", content = "Contenido", orderIndex = 1)
        val exercise = ExerciseEntity(id = "ex_01", lessonId = "l1", type = "UNIQUE_CHOICE", instruction = "¿Qué es C#?", answer = "Lenguaje", xp = 10, orderIndex = 1)

        userDao.insert(user)
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(listOf(lesson))
        exerciseDao.insertAll(listOf(exercise))

        val attempt = ExerciseAttemptEntity(
            exerciseId = "ex_01",
            userId = "user_001",
            lessonId = "l1",
            exerciseType = "UNIQUE_CHOICE",
            isCompleted = true,
            attemptsCount = 1,
            correctCount = 1,
            incorrectCount = 0,
            lastIsCorrect = true,
            lastAttemptAt = System.currentTimeMillis()
        )

        // Act
        exerciseAttemptDao.insertOrUpdate(attempt)
        val completedIds = exerciseAttemptDao.getCompletedExerciseIdsForLesson("user_001", "l1")
        val retrievedAttempt = exerciseAttemptDao.getAttempt("user_001", "ex_01")

        // Assert
        assertEquals(1, completedIds.size)
        assertEquals("ex_01", completedIds[0])
        assertNotNull(retrievedAttempt)
        assertTrue(retrievedAttempt!!.isCompleted)
        assertEquals(1, retrievedAttempt.correctCount)
    }

    /**
     * PI-02: Verifies bulk retrieval of exercise IDs from Room database.
     */
    @Test
    fun verifyBatchRetrievalOfExerciseIds() = runBlocking {
        // Arrange
        val course = CourseEntity(id = "c_batch", title = "Curso Batch", description = "Desc", track = "c_sharp", orderIndex = 1)
        val lesson = LessonEntity(id = "l_batch", courseId = "c_batch", title = "Leccion", content = "Contenido", orderIndex = 1)
        val exercises = listOf(
            ExerciseEntity(id = "ex_batch_1", lessonId = "l_batch", type = "UNIQUE_CHOICE", instruction = "Opcion 1", orderIndex = 1),
            ExerciseEntity(id = "ex_batch_2", lessonId = "l_batch", type = "COMPLETE_CODE", instruction = "Opcion 2", orderIndex = 2)
        )

        // Act
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(listOf(lesson))
        exerciseDao.insertAll(exercises)
        val retrievedIds = exerciseDao.getAllExerciseIds()

        // Assert
        assertEquals(2, retrievedIds.size)
        assertTrue(retrievedIds.contains("ex_batch_1"))
        assertTrue(retrievedIds.contains("ex_batch_2"))
    }

    // ============================================================================================
    //  PI-03: Persistencia y Consulta de Actividad Diaria en Calendario Anual
    // ============================================================================================

    /**
     * PI-03: Verifies daily activity recording and yearly aggregation in Room.
     */
    @Test
    fun verifyDailyActivityPersistenceAndAccumulation() = runBlocking {
        // Arrange
        val user = UserEntity(id = "user_002", name = "Ana Torres", email = "ana@upn.pe", avatarId = "avatar_002")
        val date = "2026-10-12"
        val year = 2026
        val activity = DailyActivityEntity(
            id = "user_002_$date",
            userId = "user_002",
            date = date,
            year = year,
            exercisesCount = 5,
            correctCount = 4,
            incorrectCount = 1,
            xpEarned = 80,
            lessonsCount = 1
        )

        // Act
        userDao.insert(user)
        dailyActivityDao.insertOrUpdate(activity)
        val saved = dailyActivityDao.getActivityForDate("user_002", date)
        val yearActivities = dailyActivityDao.getAllActivitiesForYear(year).first()

        // Assert
        assertNotNull(saved)
        assertEquals(5, saved!!.exercisesCount)
        assertEquals(80, saved.xpEarned)
        assertTrue(yearActivities.any { it.date == date && it.xpEarned == 80 })
    }

    // ============================================================================================
    //  PI-04: Persistencia del Motor FSRS-6 en Base de Datos Local - Offline-First
    // ============================================================================================

    /**
     * PI-04: Verifies FSRS offline card persistence and overdue queue filtering in SQLite.
     */
    @Test
    fun verifyOfflineFsrsCardsPersistenceAndOverdueFilter() = runBlocking {
        // Arrange
        val user = UserEntity(id = "user_003", name = "Carlos Kipu", email = "carlos@upn.pe", avatarId = "avatar_003")
        val course = CourseEntity(id = "c2", title = "C# OOP", description = "Desc", track = "c_sharp", orderIndex = 2)
        val lesson = LessonEntity(id = "l2", courseId = "c2", title = "Clases", content = "POO", orderIndex = 1)
        val ex1 = ExerciseEntity(id = "ex_card_01", lessonId = "l2", type = "FLASHCARD", instruction = "¿Qué es una clase?", answer = "Plantilla", xp = 15, orderIndex = 1)
        val ex2 = ExerciseEntity(id = "ex_card_02", lessonId = "l2", type = "FLASHCARD", instruction = "¿Qué es un objeto?", answer = "Instancia", xp = 15, orderIndex = 2)

        userDao.insert(user)
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(listOf(lesson))
        exerciseDao.insertAll(listOf(ex1, ex2))

        val now = System.currentTimeMillis()
        val overdueProgress = LearningProgressEntity(
            exerciseId = "ex_card_01",
            userId = "user_003",
            difficulty = 5.0,
            stability = 3.5,
            reps = 2,
            lapses = 0,
            state = 2,
            dueDate = now - 3600000L // Vencida hace 1 hora
        )
        val futureProgress = LearningProgressEntity(
            exerciseId = "ex_card_02",
            userId = "user_003",
            difficulty = 4.0,
            stability = 10.0,
            reps = 1,
            lapses = 0,
            state = 2,
            dueDate = now + 86400000L // Vence mañana
        )

        // Act
        learningProgressDao.insertAll(listOf(overdueProgress, futureProgress))
        val dueCards = learningProgressDao.getDueExercises("user_003", now).first()

        // Assert
        assertEquals("Solo la tarjeta vencida debe estar en la cola de repaso", 1, dueCards.size)
        assertEquals("ex_card_01", dueCards[0].exerciseId)
    }

    // ============================================================================================
    //  PI-05: Integridad Transaccional de Progreso de Usuario con Detalles Agregados
    // ============================================================================================

    /**
     * PI-05: Verifies transactional integrity when saving full user progress with aggregated details.
     */
    @Test
    fun verifyFullUserProgressTransactionalIntegrity() = runBlocking {
        // Arrange
        val user = UserEntity(id = "user_004", name = "Lucía Morales", email = "lucia@upn.pe", avatarId = "avatar_004")
        val course = CourseEntity(id = "c_intro", title = "Fundamentos", description = "Desc", track = "c_sharp", orderIndex = 1)
        val lesson1 = LessonEntity(id = "l_01", courseId = "c_intro", title = "L1", content = "C1", orderIndex = 1)
        val lesson2 = LessonEntity(id = "l_02", courseId = "c_intro", title = "L2", content = "C2", orderIndex = 2)

        userDao.insert(user)
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(listOf(lesson1, lesson2))

        val userProgress = UserProgressEntity(
            userId = "user_004",
            activeTrack = "c_sharp",
            lastVisitedLessonId = "l_01",
            totalXp = 350,
            streakDay = 3,
            completedAt = System.currentTimeMillis()
        )
        val completedLessons = listOf(
            UserCompletedLessonEntity(userId = "user_004", lessonId = "l_01", xpEarned = 100, completedAt = System.currentTimeMillis()),
            UserCompletedLessonEntity(userId = "user_004", lessonId = "l_02", xpEarned = 150, completedAt = System.currentTimeMillis())
        )
        val completedCourses = listOf(
            UserCompletedCourseEntity(userId = "user_004", courseId = "c_intro", completedAt = System.currentTimeMillis())
        )

        // Act
        userProgressDao.insertFullProgress(userProgress, completedLessons, completedCourses)
        val progressWithDetails = userProgressDao.getUserProgressWithDetails("user_004").first()

        // Assert
        assertNotNull(progressWithDetails)
        assertEquals("user_004", progressWithDetails!!.progress.userId)
        assertEquals(350, progressWithDetails.progress.totalXp)
        assertEquals(2, progressWithDetails.completedLessons.size)
        assertEquals(1, progressWithDetails.completedCourses.size)
        assertEquals("c_intro", progressWithDetails.completedCourses[0].courseId)
    }
}
