package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kipucode.data.local.model.LessonEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A LECCIONES -> ROOM DATABASE
// ============================================================================================
@Dao
interface LessonDao {
    //  Guardar Lecciones -> Inserción de la estructura completa de lecciones
    // ========================================================================================
    @Upsert
    suspend fun insertAll(lessons: List<LessonEntity>)


    // ========================================================================================
    //  Obtener información de una lección específica por ID
    // ========================================================================================
    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    fun getLessonById(lessonId: String): Flow<LessonEntity?>

}
