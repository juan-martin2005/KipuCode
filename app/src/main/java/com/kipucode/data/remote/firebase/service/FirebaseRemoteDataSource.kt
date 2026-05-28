package com.kipucode.data.remote.firebase.service

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.kipucode.data.remote.firebase.dto.UserDto

class FirebaseRemoteDataSource(
    private val firebaseAuth: FirebaseAuth
) {
    private val auth = Firebase.auth
    fun signInWithEmail(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email,password)
    fun registerUserWithEmail(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email,password)
    fun logoutUser() = firebaseAuth.signOut()
}