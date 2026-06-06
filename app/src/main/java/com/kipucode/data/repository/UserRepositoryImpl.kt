package com.kipucode.data.repository

import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO USER_REPOSITORY
// ===============================================================================================
class UserRepositoryImpl @Inject constructor(
    //  Instancia de UserRemoteDataSource -> Acceso a datos remotos en Firestore
    //  Instancia de CourseDao -> Acceso a operaciones locales del Usuario (Room)
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao
    ): UserRepository {
    //  ! IMPORTANTE
    //  Offline-First: Los flujos 'get' consumen directamente de Room (DB Local). La UI nunca
    //     experimenta retrasos de red al consultar datos ya almacenados.

    //  Mappers: Se usa '.map { ... }' de Flow para convertir las entidades de la base de datos
    //     (Entity) a modelos de negocio (Domain).

    //  SINCRONIZACIÓN: El 'refresh' descarga primero la estructura de cursos para traer las
    //     lecciones de cada uno desde Firestore.


    // ===========================================================================================
    //  Observa los datos locales por el UID actual -> `currentUserId`
    // ===========================================================================================
    override fun getUserProfile(): Flow<UserDomain?> {
        val currentUid = userRemoteDataSource.currentUserId
        return if (currentUid != null) {
            userDao.getUserByIdFlow(currentUid).map { it?.toDomain() }
        } else {
            flowOf(null)
        }
    }

    // ===========================================================================================
    //  Sincronización Remota -> Descarga y actualiza el perfil del usuario desde Firestore a Room
    // ===========================================================================================
    override suspend fun refreshUserProfile(): Response<Unit> {
        return try {
            val remoteProfile = userRemoteDataSource.getUserProfile()?.toEntity()
            if (remoteProfile != null) {
                userDao.insert(remoteProfile)
                Response.Success(Unit)
            } else {
                Response.Error("User profile not found in Firestore", ErrorType.FIRESTORE_ERROR)
            }
        } catch (e: Exception) {
            Response.Error("Failed to sync with network", ErrorType.FIRESTORE_ERROR)
        }
    }
}