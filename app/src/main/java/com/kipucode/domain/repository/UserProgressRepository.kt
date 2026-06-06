package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  CONTRATO DEL REPOSITORIO DE PROGRESO DE USUARIO
// ============================================================================================
interface UserProgressRepository {
    //  ! IMPORTANTE
    //  FLOW (Asíncrono): Proporciona una respuesta inmediata mapeando los datos de la DB local
    //    (Room). Emite cambios automáticamente a la UI en tiempo real sin bloquear hilos.

    //  SUSPEND (Corrutinas): Requerido debido a que la sincronización con el servidor remoto
    //      (API/Firestore) es una operación lenta que depende de la latencia de red. Remueve la
    //      ejecución del hilo principal para NO congelar la aplicación.

    // ========================================================================================
    //  Progreso del Usuario -> Obtención inmediata del progreso de usuario en la DB local
    // ========================================================================================
    fun getUserProgress(): Flow<UserProgressDomain?>

    // ========================================================================================
    //  Sincronización Remota -> Descarga datos desde la red y actualiza la DB local
    // ========================================================================================
    suspend fun refreshUserProgress(): Response<Unit>
}
