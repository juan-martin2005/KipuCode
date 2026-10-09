package com.kipucode.domain.model.excecution

data class CodeExecutionRequestDomain(
    val languageKey : String,
    val sourceCode : String,
    val stdin : String = ""
)
