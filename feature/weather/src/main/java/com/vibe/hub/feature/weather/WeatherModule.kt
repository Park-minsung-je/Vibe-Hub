package com.vibe.hub.feature.weather

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        // Kotlin 2.0+ 환경에서의 더 명확한 타입 추론을 위해 제네릭을 명시합니다.
        return retrofit.create(WeatherApiService::class.java)
    }
}