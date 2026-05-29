package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.AuthRepository

class LoginUseCases(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Response<User> {
        return authRepository.login(email, password)
    }
}

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, password: String): Response<User> {
        return authRepository.register(user, password)
    }
}

class IsUserLoggedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Response<Unit> {
        return authRepository.resetPassword(email)
    }
}

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun invoke() {
        authRepository.logout()
    }
}