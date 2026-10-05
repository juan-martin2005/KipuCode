package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.LearningProgressDto
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// ============================================================================================
//  ORIGEN DE DATOS (USER, USER_PROGRESS & LEARNING_PROGRESS) - FIRESTORE
// ============================================================================================
class UserRemoteDataSource @Inject constructor(
    // ========================================================================================
    //  Instancia de FirebaseAuth   ->  Gestionar sesión.
    //  Instancia de Firestore      ->  Interactuar con la BD NOSQL
    // ========================================================================================
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // ========================================================================================
    //  Valores constantes de los nombres de los JSONs de Firestore
    // ========================================================================================
    companion object {
        const val USERS_COLLECTION = "users"
        const val USER_PROGRESS_COLLECTION = "user_progress"
        const val LEARNING_PROGRESS_COLLECTION = "learning_progress"
    }


    // ========================================================================================
    //  Obtener el UID del usuario actual. Puede ser Nullo (?.)
    // ========================================================================================
    val currentUserId: String? get() = auth.currentUser?.uid

    // ========================================================================================
    //  Guardar el Perfil del Usuario en (USERS_COLLECTION -> users)
    // ========================================================================================
    suspend fun saveUserProfile(userDto: UserDto) {
        // Como el currentUserId puede ser NULL, de ser ese el caso se cancela la operación
        val id = currentUserId ?: return

        // Guardar en la colección (USERS_COLLECTION -> users)
        firestore.collection(USERS_COLLECTION)
            .document(id)   // Nombre del documento será el UID del usuario
            .set(userDto)                 // Guardamos los datos del usuario
            .await()                      // Espera a que termine la operación para continuar
    }

    // ========================================================================================
    //  Obtener el Perfil del Usuario en (USERS_COLLECTION -> users)
    // ========================================================================================
    suspend fun getUserProfile(): UserDto? {
        // Como el currentUserId puede ser NULL, de ser ese el caso se cancela la operación
        val id = currentUserId ?: return null

        // Buscamos y retornamos la colección (USERS_COLLECTION -> users)
        return firestore.collection(USERS_COLLECTION)
            .document(id)   // Lo buscamos por el UID del usuario
            .get()                        // Descargamos los datos del usuario
            .await()                      // Espera a que termine la operación para continuar
            .toObject<UserDto>()          // Convierte el resultado JSON a UserDto
    }

    // ========================================================================================
    //  Actualizar el avatar del usuario (USERS_COLLECTION -> users)
    // ========================================================================================
    suspend fun updateUserAvatar(avatarId : String): Void? {
        val id = currentUserId ?: return null

        return firestore.collection(USERS_COLLECTION)
            .document(id)
            .update("avatarId", avatarId)
            .await()
    }



    // ========================================================================================
    //  Crear el Progreso del Usuario en (USER_PROGRESS_COLLECTION -> user_progress)
    // ========================================================================================
    suspend fun createUserProgress(userProgressDto: UserProgressDto) {
        // Como el currentUserId puede ser NULL, de ser ese el caso se cancela la operación
        val id = currentUserId ?: return

        // Creamos la colección (USER_PROGRESS_COLLECTION -> user_progress)
        firestore.collection(USER_PROGRESS_COLLECTION)
            .document(id)       // Lo creamos con el UID del usuario
            .set(userProgressDto)             // Guardamos el progreso del usuario
            .await()                          // Espera a que termine la operación para continuar
    }

    // ========================================================================================
    //  Obtener el Progreso del Usuario en (USER_PROGRESS_COLLECTION -> user_progress)
    // ========================================================================================
    suspend fun getUserProgress(): UserProgressDto? {
        // Como el currentUserId puede ser NULL, de ser ese el caso se cancela la operación
        val id = currentUserId ?: return null

        // Buscamos y retornamos de la colección (USER_PROGRESS_COLLECTION -> user_progress)
        return firestore.collection(USER_PROGRESS_COLLECTION)
            .document(id)     // Lo buscamos por el UID del usuario
            .get()                          // Descargamos el progreso del usuario
            .await()                        // Espera a que termine la operación para continuar
            .toObject<UserProgressDto>()    // Convierte el resultado JSON a UserProgressDto
    }

    // ========================================================================================
    //  Guardar Lote de Repaso FSRS en Documento Único (LEARNING_PROGRESS_COLLECTION -> {userId})
    // ========================================================================================
    suspend fun saveLearningProgressMap(exercisesMap: Map<String, LearningProgressDto>) {
        val id = currentUserId ?: return

        firestore.collection(LEARNING_PROGRESS_COLLECTION)
            .document(id)
            .set(
                mapOf(
                    "userId" to id,
                    "updatedAt" to System.currentTimeMillis(),
                    "exercises" to exercisesMap
                ),
                SetOptions.merge()
            )
            .await()
    }

    // ========================================================================================
    //  Obtener Todos los Repasos FSRS del Usuario (1 sola lectura directa del Documento)
    // ========================================================================================
    suspend fun getAllLearningProgress(): List<LearningProgressDto> {
        val id = currentUserId ?: return emptyList()

        val docSnapshot = firestore.collection(LEARNING_PROGRESS_COLLECTION)
            .document(id)
            .get()
            .await()

        val rawMap = docSnapshot.get("exercises") as? Map<String, Any?> ?: return emptyList()

        return rawMap.values.mapNotNull { rawObj ->
            try {
                val item = rawObj as? Map<String, Any?> ?: return@mapNotNull null
                LearningProgressDto(
                    exerciseId = item["exerciseId"] as? String ?: "",
                    difficulty = (item["difficulty"] as? Number)?.toDouble() ?: 0.0,
                    stability = (item["stability"] as? Number)?.toDouble() ?: 0.0,
                    reps = (item["reps"] as? Number)?.toInt() ?: 0,
                    lapses = (item["lapses"] as? Number)?.toInt() ?: 0,
                    state = (item["state"] as? Number)?.toInt() ?: 0,
                    dueDate = (item["dueDate"] as? Number)?.toLong() ?: 0L,
                    lastReviewed = (item["lastReviewed"] as? Number)?.toLong()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
