package com.kipucode.data.remote.judge0.service

import com.kipucode.data.remote.judge0.dto.ApiResponseDto
import com.kipucode.data.remote.judge0.dto.LanguageDto
import com.kipucode.data.remote.judge0.dto.SubmissionRequestDto
import com.kipucode.data.remote.judge0.dto.SubmissionResultDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Judge0Api {
    @POST("api/v1/code/execute")
    suspend fun executeCode(
        @Body request: SubmissionRequestDto
    ): Response<ApiResponseDto<SubmissionResultDto>>

    @GET("api/v1/code/languages")
    suspend fun getLanguages(): Response<ApiResponseDto<List<LanguageDto>>>
}