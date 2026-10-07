package com.kipucode.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.DailyActivityDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LearningProgressDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.local.model.*

@Database(
    entities = [
        UserEntity::class,
        UserProgressEntity::class,
        UserCompletedLessonEntity::class,
        UserCompletedCourseEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        ExerciseEntity::class,
        BlockOptionEntity::class,
        LearningProgressEntity::class,
        DailyActivityEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun blockOptionDao(): BlockOptionDao
    abstract fun learningProgressDao(): LearningProgressDao
    abstract fun dailyActivityDao(): DailyActivityDao
}
