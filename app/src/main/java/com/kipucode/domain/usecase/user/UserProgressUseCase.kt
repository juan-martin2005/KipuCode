package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO ENCAPSULADOS EN EL USER_PROGRESS_REPOSITORY (PROGRESO GLOBAL DEL USUARIO)
// ============================================================================================

//  ! IMPORTANTE
//  RESPONSABILIDAD ÚNICA: Cada clase encapsula una única acción del negocio, permitiendo que
//     sean escalables y desacoplados de la lógica de los ViewModels.

//  INVOKE: Habilita la llamada directa de la clase como si fuese una función.

// ============================================================================================
//  CASO DE USO: OBTENER PROGRESO DEL USUARIO
// ============================================================================================
class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    operator fun invoke(): Flow<UserProgressDomain?> {
        return userProgressRepository.getUserProgress()
    }
}

// ============================================================================================
//  CASO DE USO: REFRESCAR PROGRESO DEL USUARIO
// ============================================================================================
class RefreshUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(): Response<Unit> {
        return userProgressRepository.refreshUserProgress()
    }
}
