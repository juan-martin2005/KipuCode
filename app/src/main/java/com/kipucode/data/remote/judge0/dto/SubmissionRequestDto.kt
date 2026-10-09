package com.kipucode.data.remote.judge0.dto

import com.google.gson.annotations.SerializedName

data class SubmissionRequestDto(
    @SerializedName("languageKey") val languageKey: String,
    @SerializedName("sourceCode") val sourceCode: String,
    @SerializedName("stdin") val stdin: String = "",
    @SerializedName("cpuTimeLimit") val cpuTimeLimit: Double? = null,
    @SerializedName("memoryLimit") val memoryLimit: Int? = null
)
