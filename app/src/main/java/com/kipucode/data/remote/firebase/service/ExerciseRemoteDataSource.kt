package com.kipucode.data.remote.firebase.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.ExerciseDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExerciseRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val EXERCISES_COLLECTION = "exercises"
    }

    // Consulta los ejercicios de Firestore filtrándolos por el ID de la lección
    suspend fun getExercisesByLessonId(lessonId: String): List<ExerciseDto> {
        return firestore.collection(EXERCISES_COLLECTION)
            .whereEqualTo("lessonId", lessonId)
            .get()
            .await()
            .map { document ->
                document.toObject<ExerciseDto>().copy(id = document.id)
            }
    }
}