package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kipucode.data.local.dao.relation.LessonWithExercises
import com.kipucode.data.local.model.LessonEntity

@Dao
interface LessonDao {
    @Insert
    fun insert(lesson: LessonEntity)

    @Insert
    fun insertAll(lessons: List<LessonEntity>)

    @Query("SELECT * FROM lessons")
    fun getAllLessons(): List<LessonEntity>

    @Query("SELECT * FROM lessons")
    fun getLessonWithExercises(): List<LessonWithExercises>
}
