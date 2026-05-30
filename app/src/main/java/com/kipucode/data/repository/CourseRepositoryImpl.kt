package com.kipucode.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.CourseFirestoreSource
import com.kipucode.domain.model.Course
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository

internal class CourseRepositoryImpl(
    private val remoteDataSource: CourseFirestoreSource,
    //private val localDataSource: CourseDao
): CourseRepository {

    override suspend fun getCourses(): Response<List<Course>> {
        return try {
            val remoteCourse = remoteDataSource.getCourses().map { it.toEntity() }
            Response.Success(remoteCourse.map { it.toDomain() })
        } catch (ex : FirebaseFirestoreException){
            Response.Error("Error al cargar los cursos: ${ex.message}", ErrorType.FIRESTORE_ERROR)
        }
    }
}