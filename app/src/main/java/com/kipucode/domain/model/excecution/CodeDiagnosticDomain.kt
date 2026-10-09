package com.kipucode.domain.model.excecution

data class CodeDiagnosticDomain(
    val line: Int,
    val column: Int,
    val severity: String,
    val code: String? = null,
    val message: String
)
