package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jg.kipucode.data.local.model.UserProgress

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    fun getUserProgress(userId: String): UserProgress?

    @Insert
    fun insert(userProgress: UserProgress)
}
