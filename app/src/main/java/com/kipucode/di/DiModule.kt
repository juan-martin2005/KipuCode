package com.kipucode.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.remote.firebase.service.FirebaseAuthSource
import com.kipucode.data.remote.firebase.service.FirebaseDataSource
import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.data.repository.CourseRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.usecase.course.GetCourseUseCase
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule{

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "Kipucode_database"
//        ).build()
//    }

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuthSource {
        val authSource = FirebaseAuth.getInstance()
        return FirebaseAuthSource(authSource)
    }

    @Provides
    @Singleton
    internal fun provideAuthRepositoryImpl(firebaseAuth: FirebaseAuthSource): AuthRepositoryImpl{
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    internal fun provideLoginUseCase(authRepositoryImpl: AuthRepositoryImpl): LoginUseCases{
        return LoginUseCases(authRepositoryImpl)
    }

    @Provides
    @Singleton
    internal fun provideRegisterUseCase(authRepositoryImpl: AuthRepositoryImpl): RegisterUseCase{
        return RegisterUseCase(authRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): FirebaseDataSource{
        val dataSource = FirebaseFirestore.getInstance()
        return FirebaseDataSource(dataSource)
    }

    @Provides
    @Singleton
    fun provideCourseLocalDataSource(appDatabase: AppDatabase): CourseDao{
        return appDatabase.courseDao()
    }

    @Provides
    @Singleton
    internal fun provideCourseRepositoryImpl(remoteDataSource: FirebaseDataSource) : CourseRepositoryImpl{
        return CourseRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    internal fun provideGetCoursesUseCase(courseRepositoryImpl: CourseRepositoryImpl): GetCourseUseCase{
        return GetCourseUseCase(courseRepositoryImpl)
    }

}