package com.kipucode.data.repository

import com.kipucode.data.remote.firebase.service.FirebaseRemoteDataSource
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

internal class AuthRepositoryImpl(
    private val remoteDataSource: FirebaseRemoteDataSource
): AuthRepository {

    override suspend fun login(email: String, password: String): Response<User> {
        return try {
            val authResult = remoteDataSource.signInWithEmail(email,password).await()

            val currentUser = authResult.user

            if(currentUser != null){

                val user = User(currentUser.uid,currentUser.displayName,currentUser.email)

                Response.Success(user)
            } else {
                Response.Error("No hay usuario registrado", null)

            }

        }catch (ex: Exception){
            Response.Error(ex.message, null)
        }

    }

    override suspend fun register(user: User, password: String): Response<User> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }


}