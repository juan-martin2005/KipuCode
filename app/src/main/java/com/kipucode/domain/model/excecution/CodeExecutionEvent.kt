package com.kipucode.domain.model.excecution

sealed interface CodeExecutionEvent {
    data object Idle : CodeExecutionEvent
    data object Running : CodeExecutionEvent
    data class Success(val result: CodeExecutionResultDomain) : CodeExecutionEvent
    data class Error(
        val message: String,
        val diagnostics: List<CodeDiagnosticDomain> = emptyList(),
        val rawOutput: String? = null
    ) : CodeExecutionEvent
}