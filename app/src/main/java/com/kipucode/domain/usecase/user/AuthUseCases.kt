package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository

class LoginUseCases (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Response<User>{
        if(email.isBlank()) return Response.Error("Email is required", ErrorType.EMAIL_EMPTY)
        if(password.isBlank()) return Response.Error("Password is required", ErrorType.PASSWORD_EMPTY)
        if(!email.endsWith("@upn.pe")) return Response.Error("Please use your institutional email (@upn.pe)", ErrorType.EMAIL_DOMAIN_NOT_VALID)
        return authRepository.login(email,password)
    }
}

class RegisterUseCase(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(user: User, password: String): Response<User>{
        if(user.name.isBlank()) return Response.Error("Name is required", ErrorType.NAME_EMPTY)
        if(user.email.isBlank()) return Response.Error("Email is required", ErrorType.EMAIL_EMPTY)
        if(!user.email.endsWith("@upn.pe")) return Response.Error("Email is not valid use your institutional email (@upn.pe)", ErrorType.EMAIL_DOMAIN_NOT_VALID)
        return authRepository.register(user, password)
    }
}
