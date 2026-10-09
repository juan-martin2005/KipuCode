package com.kipucode.data.remote.judge0.dto

import com.google.gson.annotations.SerializedName

data class CodeDiagnosticDto(
    @SerializedName("line") val line: Int,
    @SerializedName("column") val column: Int,
    @SerializedName("severity") val severity: String,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String
)
