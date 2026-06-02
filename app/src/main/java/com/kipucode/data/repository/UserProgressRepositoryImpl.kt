package com.kipucode.data.repository

import com.kipucode.data.mapper.toDomain
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgress
import com.kipucode.domain.repository.UserProgressRepository
import javax.inject.Inject

internal class UserProgressRepositoryImpl @Inject constructor(
    private val userRemoteData: UserRemoteDataSource,
): UserProgressRepository {
    override suspend fun getUserProgress(): Response<UserProgress> {
        return try {
            val userProgressDto = userRemoteData.getUserProgress()
            if (userProgressDto != null) {
                Response.Success(userProgressDto.toDomain())
            } else {
                Response.Error(message = "Progress not found", error = ErrorType.FIRESTORE_ERROR)
            }
        }
        catch (ex: Exception){
            Response.Error(message = ex.message, error = ErrorType.FIRESTORE_ERROR)
        }
    }
}
