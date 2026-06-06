package com.kipucode.data.repository

import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO USER_PROGRESS_REPOSITORY
// ===============================================================================================
internal class UserProgressRepositoryImpl @Inject constructor(
    //  Instancia de UserRemoteDataSource -> Acceso a datos remotos en Firestore
    //  Instancia de UserProgressDao -> Acceso a operaciones locales del Progreso de Usuario (Room)
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userProgressDao: UserProgressDao
): UserProgressRepository {
    //  ! IMPORTANTE
    //  Offline-First: Los flujos 'get' consumen directamente de Room (DB Local). La UI nunca
    //     experimenta retrasos de red al consultar datos ya almacenados.

    //  Mappers: Se usa '.map { ... }' de Flow para convertir las entidades de la base de datos
    //     (Entity) a modelos de negocio (Domain).

    //  SINCRONIZACIÓN: El 'refresh' descarga primero la estructura de cursos para traer las
    //     lecciones de cada uno desde Firestore.

    // ===========================================================================================
    //  Observa los datos locales del progreso de usuario
    // ===========================================================================================
    override fun getUserProgress(): Flow<UserProgressDomain?> {
        val currentUid = userRemoteDataSource.currentUserId

        return if (currentUid != null) {
            userProgressDao.getUserProgress(currentUid).map { it?.toDomain() }
        } else {
            flowOf(null)
        }
    }

    // ===========================================================================================
    //  Sincronización Remota -> Descarga de Firestore el progreso y actualiza la DB local
    // ===========================================================================================
    override suspend fun refreshUserProgress(): Response<Unit> {
        return try {
            val userProgressDto = userRemoteDataSource.getUserProgress()

            if (userProgressDto != null) {
                userProgressDao.insert(userProgressDto.toEntity())
                Response.Success(Unit)
            }else{
                Response.Error(message = "Progress not found", error = ErrorType.FIRESTORE_ERROR)
            }
        }catch (ex: Exception){
            Response.Error(message = ex.message, error = ErrorType.FIRESTORE_ERROR)
        }
    }
}
