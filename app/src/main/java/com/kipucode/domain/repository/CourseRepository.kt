package com.kipucode.domain.repository

import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.Response
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  CONTRATO DEL REPOSITORIO DE CURSOS Y LECCIONES
// ============================================================================================
interface CourseRepository {
    //  ! IMPORTANTE
    //  FLOW (Asíncrono): Proporciona una respuesta inmediata mapeando los datos de la DB local
    //    (Room). Emite cambios automáticamente a la UI en tiempo real sin bloquear hilos.

    //  SUSPEND (Corrutinas): Requerido debido a que la sincronización con el servidor remoto
    //      (API/Firestore) es una operación lenta que depende de la latencia de red. Remueve la
    //      ejecución del hilo principal para NO congelar la aplicación.

    // ========================================================================================
    //  Obtener la lista completa de cursos con el modelo del Dominio
    // ========================================================================================
    fun getCourses(): Flow<List<CourseDomain>>

    // ========================================================================================
    //  Obtener la información actualizada de un curso por `courseId`
    // ========================================================================================
    fun getCourseById(courseId: String): Flow<CourseDomain?>


    fun getCourseWithLessons(): Flow<List<CourseWithLessonsDomain>>

    // ========================================================================================
    //  Descargar toda la estructura de cursos y lecciones de la red
    // ========================================================================================
    suspend fun refreshCoursesAndLessons(): Response<Unit>
}