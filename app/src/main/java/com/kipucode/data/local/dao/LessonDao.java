package com.kipucode.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kipucode.data.local.dao.relation.LessonWithExercises;
import com.kipucode.data.local.model.Lesson;

import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    void insert(Lesson lesson);
    @Insert
    void insertAll(List<Lesson> lessons);
    @Query("SELECT * FROM lessons")
    List<Lesson> getAllLessons();
    @Query("SELECT * FROM lessons")
    List<LessonWithExercises> getLessonWithExercises();
}
