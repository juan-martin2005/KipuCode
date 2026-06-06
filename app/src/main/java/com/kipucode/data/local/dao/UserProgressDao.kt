package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    //  SUSPEND (Corrutinas): Obliga a ejecutar las operaciones de escritura (INSERT) y borrado
    //     (DELETE) dentro de un entorno asíncrono para NO congelar la pantalla del usuario.

    // ========================================================================================
    //  Flujo de Progreso en Tiempo Real (Flow)
    // ========================================================================================
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    fun getUserProgress(userId: String): Flow<UserProgressEntity?>

    // ========================================================================================
    //  Guardar o Actualizar Avance -> REPLACE para actualizar el estado de la lección actual
    // ========================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProgress: UserProgressEntity)

    // ========================================================================================
    //  Cerrar Sesión / Reinicio de Progreso -> Borrado total de la tabla de avances locales
    // ========================================================================================
    @Query("DELETE FROM user_progress")
    suspend fun clearProgressData()
}
