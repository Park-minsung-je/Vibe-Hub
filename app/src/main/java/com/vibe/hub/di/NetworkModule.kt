package com.vibe.hub.di

import com.vibe.hub.data.api.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @Module: Hilt 모듈임을 선언합니다.
 * Spring의 @Configuration과 같은 역할을 합니다. 객체 생성 방법을 Hilt에게 알려줍니다.
 * 
 * @InstallIn(SingletonComponent::class): 이 모듈의 생명주기를 설정합니다.
 * SingletonComponent는 앱이 살아있는 동안 단 하나만 생성되어 공유됨을 의미합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://vibe.weather.ilf.kr" // 실제 서버 주소

    /**
     * @Provides: 의존성 객체를 생성하여 제공하는 메서드입니다.
     * Spring의 @Bean과 같은 역할을 합니다.
     * 
     * @Singleton: 앱 전체에서 하나의 인스턴스만 사용하도록 보장합니다.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}
