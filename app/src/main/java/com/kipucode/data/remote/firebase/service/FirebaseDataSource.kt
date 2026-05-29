package com.kipucode.data.remote.firebase.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kipucode.data.remote.firebase.dto.CourseDto
import com.kipucode.data.remote.firebase.dto.LessonDto
import kotlinx.coroutines.tasks.await

class FirebaseDataSource(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val COURSE_COLLECTION = "courses"
        const val LESSON_COLLECTION = "lessons"
    }

    // COURSE - GET
    suspend fun getCourses(): List<CourseDto>{
        return firestore.collection(COURSE_COLLECTION)
            .get()
            .await()
            .map { it.toObject<CourseDto>().copy(id = it.id) }
    }

    suspend fun getLessonByCourseId(courseId: String): List<LessonDto>  = firestore.collection(LESSON_COLLECTION)
        .whereEqualTo("courseId", courseId)
        .get()
        .await()
        .map { it.toObject<LessonDto>().copy(id = it.id) }

    // COURSE - CREATE
    suspend fun saveCourse(course: CourseDto){
        val courseRef = firestore.collection(COURSE_COLLECTION).document()
        courseRef.set(course.copy(id = courseRef.id)).await()
    }


    //LESSON

    //EXERCISES


}