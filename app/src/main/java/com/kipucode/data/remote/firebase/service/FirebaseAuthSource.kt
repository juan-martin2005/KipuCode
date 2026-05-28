package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthSource(
    private val firebaseAuth: FirebaseAuth
) {
    fun signInWithEmail(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email,password)
    fun registerUserWithEmail(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email,password)
    fun logoutUser() = firebaseAuth.signOut()
}