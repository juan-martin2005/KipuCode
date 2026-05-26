package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Response<User>
    suspend fun register(user: User, password: String): Response<User>
    suspend fun logout()
}