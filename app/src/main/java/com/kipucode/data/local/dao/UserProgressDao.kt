package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kipucode.data.local.dao.relation.UserProgressWithDetails
import com.kipucode.data.local.model.UserCompletedCourseEntity
import com.kipucode.data.local.model.UserCompletedLessonEntity
import com.kipucode.data.local.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A DATOS DE PROGRESO DE USUARIO -> ROOM DATABASE
// ============================================================================================
@Dao
interface UserProgressDao {

    @Transaction
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    fun getUserProgressWithDetails(userId: String): Flow<UserProgressWithDetails?>

    @Query("UPDATE user_progress SET last_visited_lesson_id = :lessonId WHERE user_id = :userId")
    suspend fun updateLastVisitedLesson(userId: String, lessonId: String)

    @Upsert
    suspend fun insert(userProgress: UserProgressEntity)

    @Upsert
    suspend fun insertCompletedLessons(lessons: List<UserCompletedLessonEntity>)

    @Upsert
    suspend fun insertCompletedCourses(courses: List<UserCompletedCourseEntity>)

    @Query("SELECT * FROM user_completed_lessons WHERE user_id = :userId")
    suspend fun getCompletedLessonsDirect(userId: String): List<UserCompletedLessonEntity>

    @Transaction
    suspend fun insertFullProgress(
        progress: UserProgressEntity,
        completedLessons: List<UserCompletedLessonEntity>,
        completedCourses: List<UserCompletedCourseEntity>
    ) {
        insert(progress)
        if (completedLessons.isNotEmpty()) {
            val existingLessons = getCompletedLessonsDirect(progress.userId).associateBy { it.lessonId }
            val mergedLessons = completedLessons.map { incoming ->
                existingLessons[incoming.lessonId]?.let { existing ->
                    incoming.copy(completedAt = existing.completedAt)
                } ?: incoming
            }
            insertCompletedLessons(mergedLessons)
        }
        if (completedCourses.isNotEmpty()) {
            insertCompletedCourses(completedCourses)
        }
    }

    @Query("DELETE FROM user_progress")
    suspend fun clearProgressTable()

    @Query("DELETE FROM user_completed_lessons")
    suspend fun clearCompletedLessonsTable()

    @Query("DELETE FROM user_completed_courses")
    suspend fun clearCompletedCoursesTable()

    @Transaction
    suspend fun clearProgressData() {
        clearProgressTable()
        clearCompletedLessonsTable()
        clearCompletedCoursesTable()
    }
}
