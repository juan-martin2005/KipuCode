package com.kipucode.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.remote.firebase.service.AuthRemoteDataSource
import com.kipucode.data.remote.firebase.service.CourseFirestoreSource
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.data.repository.CourseRepositoryImpl
import com.kipucode.data.repository.UserProgressRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.repository.UserProgressRepository
import com.kipucode.domain.usecase.course.GetCourseUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.IsUserLoggedInUseCase
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.LogoutUseCase
import com.kipucode.domain.usecase.user.RegisterUseCase
import com.kipucode.domain.usecase.user.ResetPasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule {

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(): AuthRemoteDataSource {
        return AuthRemoteDataSource(FirebaseAuth.getInstance())
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(): UserRemoteDataSource {
        return UserRemoteDataSource(
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
    }

    @Provides
    @Singleton
    internal fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        userRemoteDataSource: UserRemoteDataSource,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(
            remoteAuthSource = authRemoteDataSource,
            userRemoteDataSource = userRemoteDataSource,
            userDao = userDao
        )
    }

    @Provides
    @Singleton
    internal fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCases {
        return LoginUseCases(authRepository)
    }

    @Provides
    @Singleton
    internal fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    internal fun provideIsUserLoggedInUseCase(authRepository: AuthRepository): IsUserLoggedInUseCase {
        return IsUserLoggedInUseCase(authRepository)
    }

    @Provides
    @Singleton
    internal fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }

    @Provides
    @Singleton
    internal fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): CourseFirestoreSource {
        val dataSource = FirebaseFirestore.getInstance()
        return CourseFirestoreSource(dataSource)
    }

    @Provides
    @Singleton
    fun provideCourseLocalDataSource(appDatabase: AppDatabase): CourseDao {
        return appDatabase.courseDao()
    }

    @Provides
    @Singleton
    internal fun provideCourseRepositoryImpl(remoteDataSource: CourseFirestoreSource): CourseRepositoryImpl {
        return CourseRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    internal fun provideGetCoursesUseCase(courseRepositoryImpl: CourseRepositoryImpl): GetCourseUseCase {
        return GetCourseUseCase(courseRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "Kipucode_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserLocalDataSource(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    internal fun provideUserProgressRepositoryImpl(remoteDataSource: UserRemoteDataSource): UserProgressRepositoryImpl {
        return UserProgressRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    internal fun provideGetUserProgressUseCase(userProgressRepositoryImpl: UserProgressRepositoryImpl): GetUserProgressUseCase {
        return GetUserProgressUseCase(userProgressRepositoryImpl)
    }
}
