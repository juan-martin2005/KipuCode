package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kipucode.data.local.dao.relation.CourseWithLessons
import com.kipucode.data.local.model.CourseEntity

@Dao
interface CourseDao {
    @Insert
    fun insert(course: CourseEntity)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): List<CourseEntity>

    @Transaction
    @Query("SELECT * FROM courses ORDER BY order_index ASC")
    fun getCourseWithLessons(): List<CourseWithLessons>
}
