package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.kipucode.data.local.model.SyncQueue;

@Dao
public interface SyncQueueDao {
    @Insert
    void insert(SyncQueue syncQueue);
    @Delete
    void delete(SyncQueue syncQueue);
}
