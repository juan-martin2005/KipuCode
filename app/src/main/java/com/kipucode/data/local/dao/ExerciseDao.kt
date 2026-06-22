package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kipucode.data.local.dao.relation.ExerciseWithOptions
import com.kipucode.data.local.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    @Transaction
    @Query("SELECT * FROM exercises WHERE lesson_id = :lessonId ORDER BY order_index ASC")
    fun getExercisesByLessonId(lessonId: String): Flow<List<ExerciseWithOptions>>
}
