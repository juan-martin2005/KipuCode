package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.kipucode.data.local.model.BlockOptionEntity

@Dao
interface BlockOptionDao {
    @Insert
    fun insert(blockOption: BlockOptionEntity)
}
