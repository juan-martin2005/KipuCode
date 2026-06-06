package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.UserProgressRepository
import com.kipucode.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO ENCAPSULADOS EN EL USER_REPOSITORY & USER_PROGRESS_REPOSITORY
// ============================================================================================

//  ! IMPORTANTE
//  RESPONSABILIDAD ÚNICA: Cada clase encapsula una única acción del negocio, permitiendo que
//     sean escalables y desacoplados de la lógica de los ViewModels.

//  INVOKE: Habilita la llamada directa de la clase como si fuese una función.


// ============================================================================================
//  CASO DE USO: OBTENER PERFIL DE USUARIO
// ============================================================================================
class GetUserProfileUseCase @Inject constructor (
    private val userRepository: UserRepository
) {
    operator fun invoke (): Flow<UserDomain?>{
        return userRepository.getUserProfile()
    }
}

// ============================================================================================
//  CASO DE USO: REFRESCAR PERFIL DE USUARIO
// ============================================================================================
class RefreshUserProfileUseCase @Inject constructor (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke (): Response<Unit>{
        return userRepository.refreshUserProfile()
    }
}

// ============================================================================================
//  CASO DE USO: OBTENER PROGRESO DEL USUARIO
// ============================================================================================
class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
)
{
    operator fun invoke(): Flow<UserProgressDomain?>{
        return userProgressRepository.getUserProgress()
    }
}

// ============================================================================================
//  CASO DE USO: REFRESCAR PROGRESO DEL USUARIO
// ============================================================================================
class RefreshUserProgressUseCase @Inject constructor (
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(): Response<Unit>{
        return userProgressRepository.refreshUserProgress()
    }
}