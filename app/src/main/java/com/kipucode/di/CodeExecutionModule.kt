package com.kipucode.di

import com.kipucode.data.remote.judge0.service.Judge0Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CodeExecutionModule {

    // URL base de la API Intermedia:
    // • En emulador Android: http://10.0.2.2:3001/
    // • En dispositivo físico: usa la IP de tu PC en la red local (ej. http://192.168.1.X:3001/) o tu URL de Cloudflare Tunnel
    const val CODE_RUNNER_BASE_URL = "http://192.168.18.25:3001/"

    @Provides
    @Singleton
    fun provideCodeRunnerOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideJudge0Api(okHttpClient: OkHttpClient): Judge0Api {
        return Retrofit.Builder()
            .baseUrl(CODE_RUNNER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Judge0Api::class.java)
    }
}