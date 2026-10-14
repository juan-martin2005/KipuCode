package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  CONTRATO DEL REPOSITORIO DE PROGRESO DE USUARIO
// ============================================================================================
interface UserProgressRepository {

    // Observa el progreso del usuario en la DB local (Room)
    fun getUserProgress(): Flow<UserProgressDomain?>

    // Sincronización Remota -> Descarga datos desde la red y actualiza la DB local
    suspend fun refreshUserProgress(): Response<Unit>

    // Persistencia pura -> Guarda el progreso del usuario en la base de datos local
    suspend fun saveUserProgress(userProgress: UserProgressDomain): Response<Unit>

    // Actualiza la última lección visitada en la base de datos local
    suspend fun updateLastVisitedLessonLocal(lessonId: String): Response<Unit>
}
