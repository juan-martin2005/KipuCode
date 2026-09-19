package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kipucode.data.local.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A DATOS DE PROGRESO DE USUARIO -> ROOM DATABASE
// ============================================================================================
@Dao
interface UserProgressDao {
    //  ! IMPORTANTE
    //  FLOW (Asíncrono): Mantiene la UI actualizada en tiempo real ante cualquier cambio
    //     en la base de datos sin bloquear el hilo principal. No requiere 'suspend'.

    //  SUSPEND (Corrutinas): Obliga a ejecutar las operaciones de escritura (INSERT/UPDATE) y borrado
    //     (DELETE) dentro de un entorno asíncrono para NO congelar la pantalla del usuario.

    // ========================================================================================
    //  Flujo de Progreso en Tiempo Real (Flow)
    // ========================================================================================
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    fun getUserProgress(userId: String): Flow<UserProgressEntity?>

    // ========================================================================================
    //  Guardar o Actualizar Avance -> UPSERT para inserción o actualización limpia
    // ========================================================================================
    @Upsert
    suspend fun insert(userProgress: UserProgressEntity)

    // ========================================================================================
    //  Cerrar Sesión / Reinicio de Progreso -> Borrado total de la tabla de avances locales
    // ========================================================================================
    @Query("DELETE FROM user_progress")
    suspend fun clearProgressData()
}
