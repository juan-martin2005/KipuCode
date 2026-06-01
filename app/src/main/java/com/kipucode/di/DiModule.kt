package com.kipucode.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.UserDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.remote.firebase.service.FirebaseAuthSource
import com.kipucode.data.remote.firebase.service.CourseFirestoreSource
import com.kipucode.data.remote.firebase.service.UserFirestoreSource
import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.data.repository.CourseRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.usecase.course.GetCourseUseCase
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
    fun provideFirebaseAuthSource(): FirebaseAuthSource {
        val authSource = FirebaseAuth.getInstance()
        return FirebaseAuthSource(authSource)
    }

    @Provides
    @Singleton
    fun provideUserFirestoreSource(): UserFirestoreSource {
        val firestoreInstance = FirebaseFirestore.getInstance()
        return UserFirestoreSource(firestoreInstance)
    }

    @Provides
    @Singleton
    internal fun provideAuthRepository(
        firebaseAuth: FirebaseAuthSource,
        userFirestoreSource: UserFirestoreSource,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(
            remoteAuthSource = firebaseAuth,
            userFirestoreSource = userFirestoreSource,
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
        )
            .build()
    }
    @Provides
    @Singleton
    fun provideUserLocalDataSource(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}