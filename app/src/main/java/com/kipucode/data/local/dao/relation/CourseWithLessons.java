    package com.kipucode.data.local.dao.relation;

    import androidx.room.Embedded;
    import androidx.room.Relation;

    import com.kipucode.data.local.model.Course;
    import com.kipucode.data.local.model.Lesson;

    import java.util.List;

    public class CourseWithLessons {
        @Embedded
        public Course course;
        @Relation(
            parentColumn = "id",
            entityColumn = "course_id"
        )
        public List<Lesson> lessons;
    }
