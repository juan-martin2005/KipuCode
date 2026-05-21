package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.kipucode.data.local.model.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert
    fun insert(syncQueue: SyncQueueEntity)

    @Delete
    fun delete(syncQueue: SyncQueueEntity)
}
