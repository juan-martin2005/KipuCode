package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// ==========================================================================================
//  ORIGEN DE AUTENTIFICACIÓN - FIREBASEAUTH
// ==========================================================================================
class AuthRemoteDataSource @Inject constructor(
    // ======================================================================================
    //  Instancia de FirebaseAuth   ->  Gestionar sesión.
    // ======================================================================================
    private val firebaseAuth: FirebaseAuth
) {

    // ======================================================================================
    //  Registro de usuario -> FirebaseAuth solo trabaja con (Correo y Contraseña)
    // ======================================================================================
    suspend fun registerUserWithEmail(email: String, password: String): AuthResult {
        // ! IMPORTANTE
        // Al hacer el Registro, firebase automáticamente guarda el UID del usuario
        // Por eso se puede user (firebaseAuth.currentUser) para el envío del email de verificación
        // En la Implementación de esta función IMPORTANTE cerrar la sesión del usuario.
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        firebaseAuth.currentUser?.sendEmailVerification()?.await()
        return result
    }

    // ======================================================================================
    //  Inicio de sesión -> FirebaseAuth solo trabaja con (Correo y Contraseña)
    // ======================================================================================
    suspend fun signInWithEmail(email: String, password: String): AuthResult =
        firebaseAuth
        .signInWithEmailAndPassword(email, password)
        .await()

    // ======================================================================================
    //  Cambiar contraseña -> FirebaseAuth incluye una función sendPasswordResetEmail
    // ======================================================================================
    suspend fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    // ======================================================================================
    //  Utilidad Extra -> Verificar si hay una sesión activa en el dispositivo
    // ======================================================================================
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    // ======================================================================================
    //  Funcionalidad Extra -> Cerrar la sesión del dispositivo
    // ======================================================================================
    fun logoutUser() = firebaseAuth.signOut()
}
