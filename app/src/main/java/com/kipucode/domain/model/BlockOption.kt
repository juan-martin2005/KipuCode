package com.kipucode.domain.model

data class BlockOption(
    val id: String,
    val exerciseId: String,
    val content: String,
    val isCorrect: Boolean,
    val correctPosition: Int
)
