package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.model.UserEntity
import com.kipucode.data.remote.firebase.service.AuthRemoteDataSource
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val remoteAuthSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao
): AuthRepository {

    override suspend fun login(email: String, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.signInWithEmail(email, password)
            val currentUser = authResult.user

            if (currentUser != null) {
                if (currentUser.isEmailVerified) {
                    val userProfile = userRemoteDataSource.getUserProfile()
                    if (userProfile != null) {
                        userDao.insert(
                            UserEntity(
                                id = userProfile.id,
                                name = userProfile.name,
                                email = userProfile.email,
                            )
                        )
                        Response.Success(userProfile)
                    } else {
                        Response.Error(
                            "User profile data not found in database",
                            ErrorType.FIRESTORE_ERROR
                        )
                    }
                } else {
                    Response.Error("Email verification required", ErrorType.EMAIL_NOT_VERIFIED)
                }
            } else {
                Response.Error("Unexpected error retrieving user information", ErrorType.FIRESTORE_ERROR)
            }
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            Response.Error("The credential is invalid", ErrorType.CREDENTIAL_INVALID)
        } catch (ex: FirebaseNetworkException) {
            Response.Error("Please check your network and try again", ErrorType.NETWORK_ERROR)
        } catch (ex: Exception) {
            Response.Error("An unexpected error occurred while signing in", ErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun register(user: User, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.registerUserWithEmail(user.email, password)
            val currentUser = authResult.user

            if (currentUser != null) {
                val updatedUser = user.copy(id = currentUser.uid)
                userRemoteDataSource.saveUserProfile(updatedUser)
                userDao.insert(
                    UserEntity(
                        id = updatedUser.id,
                        name = updatedUser.name,
                        email = updatedUser.email,
                    )
                )
                Response.Success(user)
            } else {
                Response.Error("Unexpected error retrieving user information", ErrorType.FIRESTORE_ERROR)
            }
        } catch (ex: FirebaseAuthUserCollisionException) {
            Response.Error("The email is already registered", ErrorType.EMAIL_ALREADY_EXIST)
        } catch (ex: FirebaseNetworkException) {
            Response.Error("Please check your network and try again", ErrorType.NETWORK_ERROR)
        } catch (ex: Exception) {
            Response.Error("An unexpected error occurred during registration", ErrorType.FIRESTORE_ERROR)
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

    override fun isUserLoggedIn(): Boolean = remoteAuthSource.isUserLoggedIn()

    override suspend fun logout() {
        remoteAuthSource.logoutUser()
        userDao.clearUserData()
    }
}
