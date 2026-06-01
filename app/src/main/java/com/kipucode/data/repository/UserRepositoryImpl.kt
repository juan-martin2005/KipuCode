package com.kipucode.data.repository

import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.remote.firebase.service.UserFirestoreSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userFirestoreSource: UserFirestoreSource
) {
    fun getUserProfile(userId: String): Flow<User?> {
        return userDao.getUserByIdFlow(userId).map { entity ->
            entity?.let {
                User(
                    id = it.id,
                    name = it.name ?: "",
                    email = it.email ?: "",
                    totalXp = it.totalXp,
                    streakDay = it.streakDay
                )
            }
        }
    }

    suspend fun refreshUserProfile(userId: String): Response<Unit> {
        return try {
            val remoteProfile = userFirestoreSource.getUserProfile(userId)
            if (remoteProfile != null) {
                userDao.insert(
                    UserEntity(
                        id = remoteProfile.id,
                        name = remoteProfile.name,
                        email = remoteProfile.email,
                        totalXp = remoteProfile.totalXp,
                        streakDay = remoteProfile.streakDay
                    )
                )
                Response.Success(Unit)
            } else {
                Response.Error("User profile not found in Firestore", ErrorType.FIRESTORE_ERROR)
            }
        } catch (e: Exception) {
            Response.Error("Failed to sync with network", ErrorType.FIRESTORE_ERROR)
        }
    }

    suspend fun clearLocalData() {
        userDao.clearUserData()
    }
}