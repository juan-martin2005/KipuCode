package com.kipucode.data.remote.judge0.dto

import com.google.gson.annotations.SerializedName

data class LanguageDto(
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("template") val template: String
)
