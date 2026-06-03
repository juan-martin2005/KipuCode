package com.kipucode.data.repository

import android.util.Log
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {


    suspend fun clearLocalData() {
        userDao.clearUserData()
    }

    override suspend fun getUserProfile(): Flow<User?> {
        val currentUid = userRemoteDataSource.currentUserId
        return if (currentUid != null) {
            userDao.getUserByIdFlow(currentUid).map { it?.toDomain() }
        } else {
            flowOf(null)
        }
    }

    override suspend fun refreshUserProfile(): Response<Unit> {
        return try {
            val remoteProfile = userRemoteDataSource.getUserProfile()?.toEntity()
            if (remoteProfile != null) {
                userDao.insert(remoteProfile)
                Response.Success(Unit)
            } else {
                Response.Error("User profile not found in Firestore", ErrorType.FIRESTORE_ERROR)
            }
        } catch (e: Exception) {
            Response.Error("Failed to sync with network", ErrorType.FIRESTORE_ERROR)
        }
    }

}
