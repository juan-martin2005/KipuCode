package com.kipucode.data.remote.judge0.dto

import com.google.gson.annotations.SerializedName

data class SubmissionResultDto(
    @SerializedName("status") val status: String,
    @SerializedName("stdout") val stdout: String? = null,
    @SerializedName("stderr") val stderr: String? = null,
    @SerializedName("compileOutput") val compileOutput: String? = null,
    @SerializedName("executionTimeSeconds") val executionTimeSeconds: Double? = null,
    @SerializedName("memoryKb") val memoryKb: Int? = null,
    @SerializedName("diagnostics") val diagnostics: List<CodeDiagnosticDto> = emptyList(),
    @SerializedName("judge0StatusDescription") val judge0StatusDescription: String? = null
)
