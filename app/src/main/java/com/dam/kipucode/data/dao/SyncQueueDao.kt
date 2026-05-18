package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.jg.kipucode.data.local.model.SyncQueue

@Dao
interface SyncQueueDao {
    @Insert
    fun insert(syncQueue: SyncQueue)

    @Delete
    fun delete(syncQueue: SyncQueue)
}
