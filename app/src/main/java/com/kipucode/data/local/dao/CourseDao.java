package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.kipucode.data.local.dao.relation.CourseWithLessons;
import com.kipucode.data.local.model.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    void insert(Course course);
    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();
    @Transaction
    @Query("SELECT * FROM courses ORDER BY order_index ASC")
    List<CourseWithLessons> getCourseWithLessons();

}
