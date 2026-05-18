package com.dam.kipucode.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jg.kipucode.data.local.dao.relation.LessonWithExercises
import com.jg.kipucode.data.local.model.Lesson

@Dao
interface LessonDao {
    @Insert
    fun insert(lesson: Lesson)

    @Insert
    fun insertAll(lessons: List<Lesson>)

    @Query("SELECT * FROM lessons")
    fun getAllLessons(): List<Lesson>

    @Query("SELECT * FROM lessons")
    fun getLessonWithExercises(): List<LessonWithExercises>
}
