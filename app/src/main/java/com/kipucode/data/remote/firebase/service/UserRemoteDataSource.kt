package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import com.kipucode.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val USER_PROGRESS_COLLECTION = "user_progress"
    }

    val currentUserId: String? get() = auth.currentUser?.uid

    suspend fun saveUserProfile(user: User) {
        val id = currentUserId ?: return
        firestore.collection(USERS_COLLECTION)
            .document(id)
            .set(user)
            .await()
    }

    suspend fun getUserProfile(): UserDto? {
        val id = currentUserId ?: return null
        return firestore.collection(USERS_COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject<UserDto>()
    }

    suspend fun getUserProgress(): UserProgressDto? {
        val id = currentUserId ?: return null
        val query = firestore.collection(USER_PROGRESS_COLLECTION)
            .whereEqualTo("userId", id)
            .get()
            .await()

        return if (!query.isEmpty) query.documents.first().toObject<UserProgressDto>() else null

    }
}
