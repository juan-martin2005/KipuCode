package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.jg.kipucode.data.local.dao.relation.CourseWithLessons
import com.jg.kipucode.data.local.model.Course

@Dao
interface CourseDao {
    @Insert
    fun insert(course: Course)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): List<Course>

    @Transaction
    @Query("SELECT * FROM courses ORDER BY order_index ASC")
    fun getCourseWithLessons(): List<CourseWithLessons>
}
