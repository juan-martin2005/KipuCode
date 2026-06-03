package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor (
    private val userRepository: UserRepository
) {

    suspend operator fun invoke (): Flow<User?>{
        return userRepository.getUserProfile()
    }

}

class RefreshUserProfileUseCase @Inject constructor (
    private val userRepository: UserRepository
) {

    suspend operator fun invoke (): Response<Unit>{
        return userRepository.refreshUserProfile()
    }

}

