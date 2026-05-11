package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kipucode.data.local.model.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

}
