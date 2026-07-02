package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain

interface AuthRepository {
    suspend fun login(email: String, password: String): Response<UserDomain>
    suspend fun register(userDomain: UserDomain, password: String, courseSelected: String): Response<UserDomain>
    suspend fun resetPassword(email: String): Response<Unit>
    suspend fun logout()
    fun isUserLoggedIn(): Boolean
}