package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kipucode.data.local.dao.relation.CourseWithLessons
import com.kipucode.data.local.model.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert
    fun insertCourses(course: List<CourseEntity>)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Transaction
    @Query("SELECT * FROM courses ORDER BY order_index ASC")
    fun getCourseWithLessons(): Flow<List<CourseWithLessons>>
}
