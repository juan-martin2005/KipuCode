package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kipucode.data.local.dao.relation.CourseWithLessons
import com.kipucode.data.local.model.CourseEntity
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  INTERFAZ DAO PARA ACCESO A CURSOS Y LECCIONES -> ROOM DATABASE
// ============================================================================================
@Dao
interface CourseDao {
    //  ! IMPORTANTE
    //  TRANSACTION: Obliga a Room a realizar múltiples consultas como una sola.
    //     Si una consulta falla, se revierte. CRÍTICO para relaciones de tablas (1 a Muchos).

    //  FLOW (Asíncrono): Mantiene la UI actualizada en tiempo real ante cualquier cambio
    //     en la base de datos sin bloquear el hilo principal. No requiere 'suspend'.

    //  SUSPEND (Corrutinas): Obliga a ejecutar las operaciones de escritura (INSERT) y borrado
    //     (DELETE) dentro de un entorno asíncrono para NO congelar la pantalla del usuario.

    // ========================================================================================
    //  Guardar Cursos -> Inserción de la lista de cursos
    // ========================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    // ========================================================================================
    //  Obtener todos los cursos disponibles de la bd local (Flow)
    // ========================================================================================
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    // ========================================================================================
    //  Obtener cursos con sus respectivas Lecciones ordenados por posición
    // ========================================================================================
    @Transaction
    @Query("SELECT * FROM courses ORDER BY order_index ASC")
    fun getCourseWithLessons(): Flow<List<CourseWithLessons>>

    // ========================================================================================
    //  Obtener la información de un solo curso mediante su ID
    // ========================================================================================
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseByIdFlow(courseId: String): Flow<CourseEntity?>
}
