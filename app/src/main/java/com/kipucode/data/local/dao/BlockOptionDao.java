package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.kipucode.data.local.model.BlockOption;

@Dao
public interface BlockOptionDao {
    @Insert
    void insert(BlockOption blockOption);

}
