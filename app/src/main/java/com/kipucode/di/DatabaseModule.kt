package com.kipucode.di

import android.content.Context
import androidx.room.Room
import com.kipucode.data.local.dao.*
import com.kipucode.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "Kipucode_database"
        )
        .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    fun provideUserProgressDao(appDatabase: AppDatabase): UserProgressDao = appDatabase.userProgressDao()

    @Provides
    fun provideCourseDao(appDatabase: AppDatabase): CourseDao = appDatabase.courseDao()

    @Provides
    fun provideLessonDao(appDatabase: AppDatabase): LessonDao = appDatabase.lessonDao()

    @Provides
    fun provideExerciseDao(appDatabase: AppDatabase): ExerciseDao = appDatabase.exerciseDao()

    @Provides
    fun provideBlockOptionDao(appDatabase: AppDatabase): BlockOptionDao = appDatabase.blockOptionDao()
}
