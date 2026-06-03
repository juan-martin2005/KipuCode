package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserProfile() : Flow<User?>
    suspend fun refreshUserProfile() : Response<Unit>
}