package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.kipucode.data.local.model.Exercise;

@Dao
public interface ExerciseDao {
    @Insert
    void insert(Exercise exercise);
}
