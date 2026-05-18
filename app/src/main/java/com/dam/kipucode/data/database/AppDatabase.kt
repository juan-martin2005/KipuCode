package com.dam.kipucode.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dam.kipucode.data.dao.*
import com.jg.kipucode.data.local.model.*

@Database(
    entities = [
        User::class,
        UserProgress::class,
        Course::class,
        Lesson::class,
        Exercise::class,
        BlockOption::class,
        SyncQueue::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun blockOptionDao(): BlockOptionDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Kipucode_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
