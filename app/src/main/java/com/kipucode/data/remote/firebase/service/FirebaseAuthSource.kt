package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signInWithEmail(
        email: String, password: String
    ): AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    suspend fun registerUserWithEmail(
        email: String, password: String
    ): AuthResult {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

        firebaseAuth.currentUser?.sendEmailVerification()?.await()

        return result
    }

    suspend fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logoutUser() = firebaseAuth.signOut()
}