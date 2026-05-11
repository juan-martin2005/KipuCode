package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kipucode.data.local.model.UserProgress;

@Dao
public interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    UserProgress getUserProgress(int userId);
    @Insert
    void insert(UserProgress userProgress);
}
