package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.jg.kipucode.data.local.model.Exercise

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise)
}
