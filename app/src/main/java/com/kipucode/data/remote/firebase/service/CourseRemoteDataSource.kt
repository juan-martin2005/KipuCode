package com.kipucode.data.remote.firebase.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.CourseDto
import com.kipucode.data.remote.firebase.dto.LessonDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val COURSE_COLLECTION = "courses"
        const val LESSON_COLLECTION = "lessons"
    }

    // COURSE - GET
    suspend fun getCourses(): List<CourseDto> =
        firestore.collection(COURSE_COLLECTION)
            .get()
            .await()
            .map { it.toObject<CourseDto>().copy(id = it.id) }

    suspend fun getCourseById(courseId: String): CourseDto? {
        val snapshot = firestore.collection(COURSE_COLLECTION)
            .document(courseId)
            .get()
            .await()

        return snapshot.toObject<CourseDto>()?.copy(id = snapshot.id)
    }
}