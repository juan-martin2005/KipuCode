package com.kipucode.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kipucode.data.local.dao.BlockOptionDao;
import com.kipucode.data.local.dao.CourseDao;
import com.kipucode.data.local.dao.ExerciseDao;
import com.kipucode.data.local.dao.LessonDao;
import com.kipucode.data.local.dao.SyncQueueDao;
import com.kipucode.data.local.dao.UserDao;
import com.kipucode.data.local.dao.UserProgressDao;
import com.kipucode.data.local.model.BlockOption;
import com.kipucode.data.local.model.Course;
import com.kipucode.data.local.model.Exercise;
import com.kipucode.data.local.model.Lesson;
import com.kipucode.data.local.model.SyncQueue;
import com.kipucode.data.local.model.User;
import com.kipucode.data.local.model.UserProgress;

@Database(entities = {User.class, UserProgress.class, Course.class, Lesson.class, Exercise.class, BlockOption.class, SyncQueue.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract UserProgressDao userProgressDao();
    public abstract CourseDao courseDao();
    public abstract LessonDao lessonDao();
    public abstract ExerciseDao exerciseDao();
    public abstract BlockOptionDao blockOptionDao();
    public abstract SyncQueueDao syncQueueDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "Kipucode_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
