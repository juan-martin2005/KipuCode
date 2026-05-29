package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.kipucode.data.remote.firebase.service.FirebaseAuthSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

internal class AuthRepositoryImpl(
    private val remoteAuthSource: FirebaseAuthSource
): AuthRepository {

    override suspend fun login(email: String, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.signInWithEmail(email,password)

            val currentUser = authResult.user

            if(currentUser != null){

                val user = User(currentUser.uid,currentUser.displayName.toString(),currentUser.email.toString())

                Response.Success(user)
            } else {
                Response.Error("LOGIN ERROR", ErrorType.CREDENTIAL_INVALID)

            }

        }catch (ex: FirebaseAuthInvalidCredentialsException){
            Response.Error("The credential is invalid", ErrorType.CREDENTIAL_INVALID)
        }

    }

    override suspend fun register(user: User, password: String): Response<User> {
        return try {
            val authResult = remoteAuthSource.registerUserWithEmail(user.email,password)

            val currentUser = authResult.user

            if(currentUser != null){

                val user = User(currentUser.uid,currentUser.displayName.toString(),currentUser.email.toString())

                Response.Success(user)
            } else {

                Response.Error("REGISTER ERROR", ErrorType.EMAIL_ALREADY_EXIST)

            }

        }catch (ex: Exception){
            Log.w("FIREBASE ERROR: ", ex)
            Response.Error("The email already exist", ErrorType.EMAIL_ALREADY_EXIST)
        }
    }

    override suspend fun logout() {
        remoteAuthSource.logoutUser()
    }


}