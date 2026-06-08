package com.kipucode.data.remote.firebase.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.LessonDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LessonRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val LESSON_COLLECTION = "lessons"
    }

    suspend fun getLessonByCourseId(courseId: String): List<LessonDto>  =
        firestore.collection(LESSON_COLLECTION)
            .whereEqualTo("courseId", courseId)
            .get()
            .await()
            .map { it.toObject<LessonDto>().copy(id = it.id) }

    suspend fun getLessonById(lessonId: String): LessonDto? {
        val snapshot = firestore.collection(LESSON_COLLECTION)
            .document(lessonId)
            .get()
            .await()

        return snapshot.toObject<LessonDto>()?.copy(id = snapshot.id)
    }

}