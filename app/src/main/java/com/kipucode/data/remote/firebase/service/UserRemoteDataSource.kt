package com.kipucode.data.remote.firebase.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.DayActivityDto
import com.kipucode.data.remote.firebase.dto.ExerciseAttemptDto
import com.kipucode.data.remote.firebase.dto.LearningProgressDto
import com.kipucode.data.remote.firebase.dto.UserDto
import com.kipucode.data.remote.firebase.dto.UserProgressDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// ============================================================================================
//  ORIGEN DE DATOS REMOTO - FIRESTORE
class UserRemoteDataSource @Inject constructor(
    // ========================================================================================
    //  Instancia de FirebaseAuth   ->  Gestionar sesión.
    //  Instancia de Firestore      ->  Interactuar con la BD NoSQL
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // ========================================================================================
    //  Nombres de Colecciones en Firestore
    companion object {
        const val USERS_COLLECTION = "users"
        const val USER_PROGRESS_COLLECTION = "user_progress"
        const val LEARNING_PROGRESS_COLLECTION = "learning_progress"
        const val ACTIVITY_CALENDAR_COLLECTION = "activity_calendar"
        const val EXERCISE_ATTEMPTS_COLLECTION = "exercise_attempts"
    }

    // ========================================================================================
    //  UID del usuario actual en sesión (null si no ha iniciado sesión)
    val currentUserId: String? get() = auth.currentUser?.uid

    // ========================================================================================
    //  BLOQUE 1: LECTURAS (GET) -> PROXIMAMENTE (FETCH) `BUSCAR / CARGAR`
    // ========================================================================================
    //  Obtener el Perfil del Usuario en (USERS_COLLECTION -> users)
    suspend fun getUserProfile(): UserDto? {
        val id = currentUserId ?: return null
        return firestore.collection(USERS_COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject<UserDto>()
    }
    // ========================================================================================
    //  Obtener el Progreso del Usuario en (USER_PROGRESS_COLLECTION -> user_progress)
    suspend fun getUserProgress(): UserProgressDto? {
        val id = currentUserId ?: return null
        return firestore.collection(USER_PROGRESS_COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject<UserProgressDto>()
    }
    // ========================================================================================
    //  Obtener Todos los Repasos FSRS del Usuario en (LEARNING_PROGRESS_COLLECTION)
    suspend fun getAllLearningProgress(): List<LearningProgressDto> {
        val id = currentUserId ?: return emptyList()

        val docSnapshot = firestore.collection(LEARNING_PROGRESS_COLLECTION)
            .document(id)
            .get()
            .await()

        val rawMap = docSnapshot.get("exercises") as? Map<*, *> ?: return emptyList()

        return rawMap.values.mapNotNull { rawObj ->
            try {
                val item = rawObj as? Map<*, *> ?: return@mapNotNull null
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
            } catch (_: Exception) {
                null
            }
        }
    }
    // ========================================================================================
    //  Obtener Todos los Días del Calendario Anual en (ACTIVITY_CALENDAR_COLLECTION)
    suspend fun getAllActivityCalendar(): Map<String, DayActivityDto> {
        val id = currentUserId ?: return emptyMap()

        val snapshot = firestore.collection(ACTIVITY_CALENDAR_COLLECTION)
            .document(id)
            .get()
            .await()

        val data = snapshot.data ?: return emptyMap()
        val daysMap = data["days"] as? Map<*, *> ?: return emptyMap()
        val result = mutableMapOf<String, DayActivityDto>()

        fun parseDayDto(rawObj: Any?): DayActivityDto? {
            val map = rawObj as? Map<*, *> ?: return null
            val exercises = (map["exercises"] as? Number)?.toInt() ?: 0
            val correct = (map["correct"] as? Number)?.toInt() ?: 0
            val incorrect = (map["incorrect"] as? Number)?.toInt() ?: 0
            val xp = (map["xp"] as? Number)?.toInt() ?: 0
            val lessons = (map["lessons"] as? Number)?.toInt() ?: 0
            return DayActivityDto(
                exercises = exercises,
                correct = correct,
                incorrect = incorrect,
                xp = xp,
                lessons = lessons
            )
        }

        daysMap.forEach { (rawDate, rawObj) ->
            val date = rawDate as? String ?: return@forEach
            parseDayDto(rawObj)?.let { result[date] = it }
        }

        return result
    }
    // ========================================================================================
    //  Obtener Todos los Intentos de Ejercicios en (EXERCISE_ATTEMPTS_COLLECTION)
    suspend fun getAllExerciseAttempts(): List<ExerciseAttemptDto> {
        val id = currentUserId ?: return emptyList()

        val docSnapshot = firestore.collection(EXERCISE_ATTEMPTS_COLLECTION)
            .document(id)
            .get()
            .await()

        val rawMap = docSnapshot.get("attempts") as? Map<*, *> ?: return emptyList()

        return rawMap.values.mapNotNull { rawObj ->
            try {
                val item = rawObj as? Map<*, *> ?: return@mapNotNull null
                ExerciseAttemptDto(
                    exerciseId = item["exerciseId"] as? String ?: "",
                    lessonId = item["lessonId"] as? String ?: "",
                    exerciseType = item["exerciseType"] as? String ?: "",
                    isCompleted = item["isCompleted"] as? Boolean ?: false,
                    attemptsCount = (item["attemptsCount"] as? Number)?.toInt() ?: 0,
                    correctCount = (item["correctCount"] as? Number)?.toInt() ?: 0,
                    incorrectCount = (item["incorrectCount"] as? Number)?.toInt() ?: 0,
                    lastIsCorrect = item["lastIsCorrect"] as? Boolean ?: false,
                    lastAttemptAt = (item["lastAttemptAt"] as? Number)?.toLong() ?: 0L
                )
            } catch (_: Exception) {
                null
            }
        }
    }


    // ========================================================================================
    //  BLOQUE 2: ESCRITURAS PUNTUALES (SAVE / UPDATE)
    // ========================================================================================
    //  Guardar el Perfil del Usuario en (USERS_COLLECTION -> users)
    suspend fun saveUserProfile(userDto: UserDto) {
        val id = currentUserId ?: return
        firestore.collection(USERS_COLLECTION)
            .document(id)
            .set(userDto)
            .await()
    }
    // ========================================================================================
    //  Actualizar el avatar del usuario en (USERS_COLLECTION -> users)
    suspend fun updateUserAvatar(avatarId: String): Void? {
        val id = currentUserId ?: return null
        return firestore.collection(USERS_COLLECTION)
            .document(id)
            .update("avatarId", avatarId)
            .await()
    }
    // ========================================================================================
    //  Crear el Progreso del Usuario en (USER_PROGRESS_COLLECTION -> user_progress)
    suspend fun createUserProgress(userProgressDto: UserProgressDto) {
        val id = currentUserId ?: return
        firestore.collection(USER_PROGRESS_COLLECTION)
            .document(id)
            .set(userProgressDto)
            .await()
    }
    // ========================================================================================
    //  Guardar Lote de Repaso FSRS en Documento Único (LEARNING_PROGRESS_COLLECTION)
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
    //  BLOQUE 3: BATCH ATÓMICO (Sincronización Consolidada en 1 solo viaje de red)
    // ========================================================================================

    // ========================================================================================
    //  Sincronización Consolidada Batch (User Progress + FSRS + Calendario + Intentos)
    // ========================================================================================
    suspend fun syncSessionBatch(
        userProgressDto: UserProgressDto? = null,
        exercisesMap: Map<String, LearningProgressDto>? = null,
        todayDate: String? = null,
        todayActivity: DayActivityDto? = null,
        exerciseAttemptsMap: Map<String, ExerciseAttemptDto>? = null
    ) {
        val id = currentUserId ?: return
        val batch = firestore.batch()

        // 1. Guardar progreso general del usuario si aplica
        if (userProgressDto != null) {
            val userProgressDoc = firestore.collection(USER_PROGRESS_COLLECTION).document(id)
            batch.set(userProgressDoc, userProgressDto)
        }

        // 2. Guardar o fusionar mapa de ejercicios FSRS si aplica
        if (!exercisesMap.isNullOrEmpty()) {
            val progressDoc = firestore.collection(LEARNING_PROGRESS_COLLECTION).document(id)
            batch.set(
                progressDoc,
                mapOf(
                    "userId" to id,
                    "updatedAt" to System.currentTimeMillis(),
                    "exercises" to exercisesMap
                ),
                SetOptions.merge()
            )
        }

        // 3. Registrar actividad diaria del calendario si aplica
        if (todayDate != null && todayActivity != null) {
            val calendarDoc = firestore.collection(ACTIVITY_CALENDAR_COLLECTION).document(id)
            batch.set(
                calendarDoc,
                mapOf(
                    "userId" to id,
                    "updatedAt" to System.currentTimeMillis(),
                    "days" to mapOf(
                        todayDate to mapOf(
                            "exercises" to todayActivity.exercises,
                            "correct" to todayActivity.correct,
                            "incorrect" to todayActivity.incorrect,
                            "xp" to todayActivity.xp,
                            "lessons" to todayActivity.lessons
                        )
                    )
                ),
                SetOptions.merge()
            )
        }

        // 4. Guardar mapa consolidado de intentos de ejercicios si aplica
        if (!exerciseAttemptsMap.isNullOrEmpty()) {
            val attemptsDoc = firestore.collection(EXERCISE_ATTEMPTS_COLLECTION).document(id)
            batch.set(
                attemptsDoc,
                mapOf(
                    "userId" to id,
                    "updatedAt" to System.currentTimeMillis(),
                    "attempts" to exerciseAttemptsMap
                ),
                SetOptions.merge()
            )
        }

        // Ejecutar todas las operaciones juntas en Firestore
        batch.commit().await()
    }
}
