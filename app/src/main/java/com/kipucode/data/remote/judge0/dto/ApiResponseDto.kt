package com.kipucode.data.remote.judge0.dto

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: ApiErrorDto? = null
)

data class ApiErrorDto(
    @SerializedName("message") val message: String,
    @SerializedName("statusCode") val statusCode: Int
)
