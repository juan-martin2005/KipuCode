package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kipucode.data.local.dao.relation.LessonWithExercises
import com.kipucode.data.local.model.LessonEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A LECCIONES -> ROOM DATABASE
// ============================================================================================
@Dao
interface LessonDao {
    //  ! IMPORTANTE
    //  TRANSACTION (Atomacidad): Obliga a Room a realizar múltiples consultas como una sola.
    //     Si una consulta falla, se revierte. CRÍTICO para relaciones de tablas (1 a Muchos).

    //  FLOW (Asíncrono): Mantiene la UI actualizada en tiempo real ante cualquier cambio
    //     en la base de datos sin bloquear el hilo principal. No requiere 'suspend'.

    //  SUSPEND (Corrutinas): Obliga a ejecutar las operaciones de escritura (INSERT) y borrado
    //     (DELETE) dentro de un entorno asíncrono para NO congelar la pantalla del usuario.

    // ========================================================================================
    //  Guardar Lección -> Inserción con estrategia de REPLACE
    // ========================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: LessonEntity)

    // ========================================================================================
    //  Guardar Lecciones -> Inserción de la estructura completa de lecciones
    // ========================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<LessonEntity>)

    // ========================================================================================
    //  Lista Total -> Obtener todas las lecciones sin importar el curso
    // ========================================================================================
    @Query("SELECT * FROM lessons")
    fun getAllLessons(): Flow<List<LessonEntity>>

    // ========================================================================================
    //  Obtener información de una lección específica por ID
    // ========================================================================================
    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    fun getLessonById(lessonId: String): Flow<LessonEntity?>

    // ========================================================================================
    //  Obtener las lecciones de un curso por su `course_id`
    // ========================================================================================
    @Query("SELECT * FROM lessons WHERE course_id = :courseId ORDER BY order_index ASC")
    fun getLessonsByCourseId(courseId: String): Flow<List<LessonEntity>>

    // ========================================================================================
    //  Cargar las lecciones junto con sus ejercicios relacionados
    // ========================================================================================
    @Transaction
    @Query("SELECT * FROM lessons")
    fun getLessonWithExercises(): Flow<List<LessonWithExercises>>
}
