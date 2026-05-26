package com.kipucode.domain.model

data class SyncQueue(
    val id: Long = 0,
    val entityType: String?,
    val entityId: String?,
    val payload: String?,
    val createdAt: Long
)
