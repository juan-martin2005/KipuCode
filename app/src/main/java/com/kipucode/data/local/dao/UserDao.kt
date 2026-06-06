package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kipucode.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A DATOS DE USUARIO -> ROOM DATABASE
// ============================================================================================
@Dao
interface UserDao {
    //  ! IMPORTANTE
    //  FLOW (Asíncrono): Mantiene la UI actualizada en tiempo real ante cualquier cambio
    //     en la base de datos sin bloquear el hilo principal. No requiere 'suspend'.

    //  SUSPEND (Corrutinas): Obliga a ejecutar las operaciones de escritura (INSERT) y borrado
    //     (DELETE) dentro de un entorno asíncrono para NO congelar la pantalla del usuario.

    // ========================================================================================
    //  Obtener usuario por ID mediante Flow
    // ========================================================================================
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    // ========================================================================================
    //  Guardar o Actualizar Usuario -> REPLACE para sobreescribir datos viejos
    // ========================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    // ========================================================================================
    //  Cerrar Sesión / Limpieza Local -> Eliminar por completo la tabla de usuarios
    // ========================================================================================
    @Query("DELETE FROM users")
    suspend fun clearUserData()
}