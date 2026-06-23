package com.kipucode.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kipucode.data.local.converter.ListConverters
import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.dao.SyncQueueDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.local.model.*

@Database(
    entities = [
        UserEntity::class,
        UserProgressEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        ExerciseEntity::class,
        BlockOptionEntity::class,
        SyncQueueEntity::class,

        FlashcardProgressEntity::class
    ],
    version = 1
)

@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun blockOptionDao(): BlockOptionDao
    abstract fun syncQueueDao(): SyncQueueDao

//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "Kipucode_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}
