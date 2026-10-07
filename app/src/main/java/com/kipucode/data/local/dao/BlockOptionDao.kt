package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.kipucode.data.local.model.BlockOptionEntity

@Dao
interface BlockOptionDao {
    @Upsert
    suspend fun insertAll(options: List<BlockOptionEntity>)
}
