package com.kipucode.data.local.dao.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kipucode.data.local.model.Exercise;
import com.kipucode.data.local.model.Lesson;

import java.util.List;

public class LessonWithExercises {
    @Embedded
    public Lesson lesson;
    @Relation(
            parentColumn = "id",
            entityColumn = "lesson_id"
    )
    public List<Exercise> exercises;
}
