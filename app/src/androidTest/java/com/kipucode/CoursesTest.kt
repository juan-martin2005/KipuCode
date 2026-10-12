package com.kipucode

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LessonEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoursesTest {

    private lateinit var database: AppDatabase
    private lateinit var courseDao: CourseDao
    private lateinit var lessonDao: LessonDao
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // In-memory SQLite database (isolated and wiped after test execution)
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        courseDao = database.courseDao()
        lessonDao = database.lessonDao()
        exerciseDao = database.exerciseDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCourses_whenFilteredByTrack_returnsCorrectCount() = runBlocking {
        // Arrange
        val courses = listOf(
            CourseEntity(id = "c1", title = "Basic Kotlin", description = "Fundamentals", track = "android", orderIndex = 1),
            CourseEntity(id = "c2", title = "Compose UI", description = "Modern UI", track = "android", orderIndex = 2),
            CourseEntity(id = "c3", title = "Node.js REST", description = "Backend API", track = "backend", orderIndex = 1)
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

    @Test
    fun insertCourseWithLessons_returnsCorrectOneToManyRelation() = runBlocking {
        // Arrange: 1 Course and 2 Lessons linked by foreign key (course_id = "c1")
        val course = CourseEntity(id = "c1", title = "Basic Kotlin", description = "Fundamentals", track = "android", orderIndex = 1)
        val lessons = listOf(
            LessonEntity(id = "l1", courseId = "c1", title = "Variables and Types", content = "Theory 1", orderIndex = 1),
            LessonEntity(id = "l2", courseId = "c1", title = "Functions and Lambdas", content = "Theory 2", orderIndex = 2)
        )

        // Act
        courseDao.insertCourses(listOf(course))
        lessonDao.insertAll(lessons)

        val coursesWithLessons = courseDao.getCourseWithLessons().first()

        // Assert
        assertEquals(1, coursesWithLessons.size)
        val firstCourse = coursesWithLessons[0]
        assertEquals("c1", firstCourse.course.id)
        assertEquals("Basic Kotlin", firstCourse.course.title)
        assertEquals(2, firstCourse.lessons.size)
        assertEquals("Variables and Types", firstCourse.lessons[0].title)
        assertEquals("Functions and Lambdas", firstCourse.lessons[1].title)
    }

    @Test
    fun insertExercises_whenQueried_returnsAllExerciseIds() = runBlocking {
        // Arrange
        val exercises = listOf(
            ExerciseEntity(id = "ex_1", lessonId = "l1", type = "UNIQUE_CHOICE", instruction = "Choose the correct option", orderIndex = 1),
            ExerciseEntity(id = "ex_2", lessonId = "l1", type = "COMPLETE_CODE", instruction = "Complete the code", orderIndex = 2)
        )

        // Act
        exerciseDao.insertAll(exercises)
        val retrievedIds = exerciseDao.getAllExerciseIds()

        // Assert
        assertEquals(2, retrievedIds.size)
        assertTrue(retrievedIds.contains("ex_1"))
        assertTrue(retrievedIds.contains("ex_2"))
    }
}
