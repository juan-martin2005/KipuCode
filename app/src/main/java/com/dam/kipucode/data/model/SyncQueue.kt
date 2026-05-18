package com.dam.kipucode.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entity_type: String?,
    val entity_id: String?,
    val payload: String?,
    val crated_at: Long
)
