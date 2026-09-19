package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kipucode.data.local.model.FlashcardProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardProgressDao {

    @Query("SELECT * FROM flashcard_progress " +
            "WHERE user_id = :userId AND next_review_date <= :currentTimeMillis ORDER BY next_review_date ASC")
    fun getDueFlashcards(userId: String, currentTimeMillis: Long): Flow<List<FlashcardProgressEntity>>

    @Query("SELECT * FROM flashcard_progress WHERE user_id = :userId AND exercise_id = :exerciseId")
    fun getProgressForExercise(userId: String, exerciseId: String): Flow<FlashcardProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: FlashcardProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progressList: List<FlashcardProgressEntity>)

    @Query("DELETE FROM flashcard_progress WHERE user_id = :userId")
    suspend fun deleteProgressForUser(userId: String)
}
