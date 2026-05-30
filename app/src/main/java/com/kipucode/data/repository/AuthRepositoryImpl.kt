package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.kipucode.R
import com.kipucode.data.remote.firebase.service.FirebaseAuthSource
import com.kipucode.data.remote.firebase.service.UserFirestoreSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val remoteAuthSource: FirebaseAuthSource,
    private val userFirestoreSource: UserFirestoreSource
): AuthRepository {

    override suspend fun login(email: String, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.signInWithEmail(email,password)
            val currentUser = authResult.user

            if(currentUser != null){
                if (currentUser.isEmailVerified) {
                    val userProfile = userFirestoreSource.getUserProfile(currentUser.uid)

                    if (userProfile != null) {
                        Response.Success(userProfile)
                    } else {
                        Response.Error(
                            "User profile data not found in database",
                            ErrorType.FIRESTORE_ERROR
                        )
                    }
                } else {
                    Response.Error("Email verification required",
                        ErrorType.EMAIL_NOT_VERIFIED)
                }
            } else {
                Response.Error("Unexpected error retrieving user information",
                    ErrorType.FIRESTORE_ERROR)
            }
        }catch (ex: FirebaseAuthInvalidCredentialsException){
            Response.Error("The credential is invalid",
                ErrorType.CREDENTIAL_INVALID)
        }catch (ex: Exception){
            Response.Error("An unexpected error occurred while signing in",
                ErrorType.FIRESTORE_ERROR)
        }

    }

    override suspend fun register(user: User, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.registerUserWithEmail(user.email,password)
            val currentUser = authResult.user

            if(currentUser != null){
                val user = user.copy(
                    id = currentUser.uid,
                    totalXp = 0,
                    streakDay = 0
                )

                userFirestoreSource.saveUserProfile(user)

                Response.Success(user)
            } else {
                Response.Error("Unexpected error retrieving user information",
                    ErrorType.FIRESTORE_ERROR)
            }
        }catch (ex: FirebaseAuthUserCollisionException) {
            Response.Error("The email is already registered",
                ErrorType.EMAIL_ALREADY_EXIST)
        }catch (ex: Exception){
            Response.Error("An unexpected error occurred during registration",
                ErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun resetPassword(email: String): Response<Unit> {
        return try {
            remoteAuthSource.sendPasswordReset(email)
            Response.Success(Unit)
        } catch (ex: Exception) {
            Log.e("AUTH_ERROR", "Error sending password reset email", ex)
            Response.Error(
                "Ocurrió un error al enviar el correo. Por favor, intenta de nuevo.",
                ErrorType.FIRESTORE_ERROR
            )
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return remoteAuthSource.isUserLoggedIn()
    }

    override suspend fun logout() {
        remoteAuthSource.logoutUser()
    }


}