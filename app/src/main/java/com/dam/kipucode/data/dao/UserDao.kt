package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jg.kipucode.data.local.model.User

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): User?
}
