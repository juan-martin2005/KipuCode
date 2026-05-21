package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kipucode.data.local.model.UserProgressEntity

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    fun getUserProgress(userId: String): UserProgressEntity?

    @Insert
    fun insert(userProgress: UserProgressEntity)
}
