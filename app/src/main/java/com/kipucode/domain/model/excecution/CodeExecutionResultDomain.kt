package com.kipucode.domain.model.excecution

data class CodeExecutionResultDomain(
    val status: String,
    val stdout: String? = null,
    val stderr: String? = null,
    val compileOutput: String? = null,
    val executionTimeSeconds: Double? = null,
    val memoryKb: Int? = null,
    val diagnostics: List<CodeDiagnosticDomain> = emptyList(),
    val statusDescription: String? = null
)
