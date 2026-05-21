package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kipucode.data.local.model.UserEntity

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): UserEntity?
}
