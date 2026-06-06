package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import kotlinx.coroutines.flow.Flow

// ============================================================================================
//  CONTRATO DEL REPOSITORIO DE USUARIO
// ============================================================================================
interface UserRepository {
    //  ! IMPORTANTE
    //  FLOW (Asíncrono): Proporciona una respuesta inmediata mapeando los datos de la DB local
    //    (Room). Emite cambios automáticamente a la UI en tiempo real sin bloquear hilos.

    //  SUSPEND (Corrutinas): Requerido debido a que la sincronización con el servidor remoto
    //      (API/Firestore) es una operación lenta que depende de la latencia de red. Remueve la
    //      ejecución del hilo principal para NO congelar la aplicación.

    // ========================================================================================
    //  Perfil Usuario -> Obtención inmediata de los datos del usuario en la DB local
    // ========================================================================================
    fun getUserProfile() : Flow<UserDomain?>

    // ========================================================================================
    //  Sincronización Remota -> Descarga datos desde la red y actualiza la DB local
    // ========================================================================================
    suspend fun refreshUserProfile() : Response<Unit>
}