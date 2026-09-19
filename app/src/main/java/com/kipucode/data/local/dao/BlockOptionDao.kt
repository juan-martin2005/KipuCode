package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kipucode.data.local.model.BlockOptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(options: List<BlockOptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blockOption: BlockOptionEntity)

    @Query("SELECT * FROM blocks_options WHERE exercise_id = :exerciseId ORDER BY order_index ASC")
    fun getOptionsByExerciseId(exerciseId: String): Flow<List<BlockOptionEntity>>
}
