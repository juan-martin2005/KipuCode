package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signInWithEmail(email: String, password: String): AuthResult = firebaseAuth.signInWithEmailAndPassword(email,password).await()
    suspend fun registerUserWithEmail(email: String, password: String): AuthResult = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
    fun logoutUser() = firebaseAuth.signOut()
}