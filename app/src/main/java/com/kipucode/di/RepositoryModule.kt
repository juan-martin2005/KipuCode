package com.kipucode.di

import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.data.repository.CourseRepositoryImpl
import com.kipucode.data.repository.LessonRepositoryImpl
import com.kipucode.data.repository.UserProgressRepositoryImpl
import com.kipucode.data.repository.UserRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.LessonRepository
import com.kipucode.domain.repository.UserProgressRepository
import com.kipucode.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    internal abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    internal abstract fun bindUserProgressRepository(
        userProgressRepositoryImpl: UserProgressRepositoryImpl
    ): UserProgressRepository

    @Binds
    @Singleton
    internal abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    internal abstract fun bindLessonRepository(
        lessonRepository: LessonRepositoryImpl
    ): LessonRepository
}
