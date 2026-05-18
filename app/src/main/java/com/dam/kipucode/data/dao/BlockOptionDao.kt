package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.jg.kipucode.data.local.model.BlockOption

@Dao
interface BlockOptionDao {
    @Insert
    fun insert(blockOption: BlockOption)
}
