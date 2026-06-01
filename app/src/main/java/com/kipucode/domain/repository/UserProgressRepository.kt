package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgress

interface UserProgressRepository {
    suspend fun getUserProgress(): Response<UserProgress>

}
