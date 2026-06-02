package com.kipucode.di

import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.data.repository.CourseRepositoryImpl
import com.kipucode.data.repository.UserProgressRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.UserProgressRepository
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
}
