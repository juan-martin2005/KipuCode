package com.kipucode.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.kipucode.data.remote.firebase.service.FirebaseAuthSource
import com.kipucode.data.repository.AuthRepositoryImpl
import com.kipucode.domain.repository.AuthRepository
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule{


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


}