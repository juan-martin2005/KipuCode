package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.kipucode.data.local.model.ExerciseEntity

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: ExerciseEntity)
}
