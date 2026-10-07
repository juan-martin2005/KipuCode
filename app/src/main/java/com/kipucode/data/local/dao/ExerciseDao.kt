package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kipucode.data.local.dao.relation.ExerciseWithOptions
import com.kipucode.data.local.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Upsert
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Transaction
    @Query("SELECT * FROM exercises WHERE lesson_id = :lessonId ORDER BY order_index ASC")
    fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseWithOptions>>

    @Transaction
    @Query("SELECT * FROM exercises ORDER BY order_index ASC")
    fun getAllExercises(): Flow<List<ExerciseWithOptions>>

    @Query("SELECT id FROM exercises")
    suspend fun getAllExerciseIds(): List<String>
}
