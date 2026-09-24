package com.kipucode.domain.model

data class BlockOptionDomain(
    val id: String,
    val exerciseId: String,
    val content: String,
    val explanation: String = "",
    val isCorrect: Boolean,
    val orderIndex: Int = 0
)
