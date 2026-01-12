package com.vibe.hub.feature.weather

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    suspend fun getRemoteWeather(lat: Double, lon: Double): Result<List<WeatherItem>> {
        return try {
            val response = apiService.getWeatherData(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace() // 디버깅을 위해 로그 출력
            Result.failure(e)
        }
    }
}
