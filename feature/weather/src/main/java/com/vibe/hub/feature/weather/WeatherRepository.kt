package com.vibe.hub.feature.weather

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double) = Result.runCatching {
        apiService.getCurrentWeather(lat, lon)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double) = Result.runCatching {
        apiService.getWeatherData(lat, lon)
    }

    suspend fun getMidTa(regId: String) = Result.runCatching {
        apiService.getMidTaForecast(regId)
    }

    suspend fun getMidLand(regId: String) = Result.runCatching {
        apiService.getMidLandForecast(regId)
    }
}