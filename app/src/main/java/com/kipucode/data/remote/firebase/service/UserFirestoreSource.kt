package com.kipucode.data.remote.firebase.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.domain.model.User
import kotlinx.coroutines.tasks.await

class UserFirestoreSource(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val USERS_COLLECTION = "users"
    }

    suspend fun saveUserProfile(user: User) {
        firestore.collection(USERS_COLLECTION)
            .document(user.id)
            .set(user)
            .await()
    }

    suspend fun getUserProfile(userId: String): User? {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<User>()
    }
}