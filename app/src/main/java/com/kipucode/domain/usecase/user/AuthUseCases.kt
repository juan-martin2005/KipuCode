package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository

class LoginUseCases (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Response<User>{
        if(email.isBlank() || password.isBlank()) Response.Error("Campos no pueden ser vacios", null)
        return authRepository.login(email,password)
    }
}

class RegisterUseCase(
    private val authRepository: AuthRepository
){

}
