package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kipucode.data.local.model.LearningProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {

    @Query("SELECT * FROM learning_progress " +
            "WHERE user_id = :userId AND due_date <= :currentTimeMillis ORDER BY due_date ASC")
    fun getDueExercises(userId: String, currentTimeMillis: Long): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE user_id = :userId AND exercise_id = :exerciseId")
    fun getProgressForExercise(userId: String, exerciseId: String): Flow<LearningProgressEntity?>

    @Query("SELECT * FROM learning_progress WHERE user_id = :userId AND exercise_id = :exerciseId")
    suspend fun getProgressForExerciseDirect(userId: String, exerciseId: String): LearningProgressEntity?

    @Query("""
        SELECT lp.* FROM learning_progress lp
        INNER JOIN exercises e ON lp.exercise_id = e.id
        WHERE e.lesson_id = :lessonId AND lp.user_id = :userId
    """)
    fun getProgressForLesson(userId: String, lessonId: String): Flow<List<LearningProgressEntity>>

    @Query("""
        SELECT lp.* FROM learning_progress lp
        INNER JOIN exercises e ON lp.exercise_id = e.id
        INNER JOIN lessons l ON e.lesson_id = l.id
        WHERE l.course_id = :courseId AND lp.user_id = :userId
    """)
    fun getProgressForCourse(userId: String, courseId: String): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE user_id = :userId")
    fun getAllProgressForUser(userId: String): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE user_id = :userId")
    suspend fun getAllProgressForUserDirect(userId: String): List<LearningProgressEntity>

    @Upsert
    suspend fun insertOrUpdate(progress: LearningProgressEntity)

    @Upsert
    suspend fun insertAll(progressList: List<LearningProgressEntity>)

    @Query("DELETE FROM learning_progress WHERE user_id = :userId")
    suspend fun deleteProgressForUser(userId: String)
}
