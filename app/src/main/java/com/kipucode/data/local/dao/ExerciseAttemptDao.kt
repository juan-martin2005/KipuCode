package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kipucode.data.local.model.ExerciseAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseAttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(attempt: ExerciseAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attempts: List<ExerciseAttemptEntity>)

    @Query("SELECT * FROM exercise_attempts WHERE user_id = :userId AND exercise_id = :exerciseId LIMIT 1")
    suspend fun getAttempt(userId: String, exerciseId: String): ExerciseAttemptEntity?

    @Query("SELECT * FROM exercise_attempts WHERE user_id = :userId AND lesson_id = :lessonId")
    suspend fun getAttemptsForLesson(userId: String, lessonId: String): List<ExerciseAttemptEntity>

    @Query("SELECT * FROM exercise_attempts WHERE user_id = :userId AND lesson_id = :lessonId")
    fun observeAttemptsForLesson(userId: String, lessonId: String): Flow<List<ExerciseAttemptEntity>>

    @Query("SELECT exercise_id FROM exercise_attempts WHERE user_id = :userId AND lesson_id = :lessonId AND is_completed = 1")
    suspend fun getCompletedExerciseIdsForLesson(userId: String, lessonId: String): List<String>

    @Query("SELECT * FROM exercise_attempts WHERE user_id = :userId")
    suspend fun getAllAttemptsForUser(userId: String): List<ExerciseAttemptEntity>

    @Query("DELETE FROM exercise_attempts WHERE user_id = :userId")
    suspend fun clearUserAttempts(userId: String)
}
