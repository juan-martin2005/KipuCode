package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgress
import com.kipucode.domain.repository.UserProgressRepository
import javax.inject.Inject

class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
)
{
    suspend operator fun invoke(): Response<UserProgress>{
        return userProgressRepository.getUserProgress()
    }
}