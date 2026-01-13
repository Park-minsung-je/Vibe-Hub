package com.vibe.hub.feature.weather

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    // 캐시 데이터 보관
    var cachedAddress: String? = null
    var cachedCurrent: List<WeatherItem>? = null
    var cachedHourly: List<WeatherItem>? = null
    var cachedMidTa: Map<String, String>? = null
    var cachedMidLand: Map<String, String>? = null
    var cachedFetchTime: String? = null

    fun hasCache(): Boolean = cachedHourly != null && cachedCurrent != null

    fun clearCache() {
        cachedAddress = null
        cachedCurrent = null
        cachedHourly = null
        cachedMidTa = null
        cachedMidLand = null
        cachedFetchTime = null
    }

    // --- API 호출 ---
    suspend fun getAddress(lat: Double, lon: Double): Result<Map<String, String>> = Result.runCatching {
        apiService.getAddress(lat, lon)
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<List<WeatherItem>> = Result.runCatching {
        apiService.getCurrentWeather(lat, lon)
    }

    suspend fun getHourlyForecast(lat: Double, lon: Double): Result<List<WeatherItem>> = Result.runCatching {
        apiService.getWeatherData(lat, lon)
    }

    suspend fun getMidTa(regId: String): Result<List<Map<String, String>>> = Result.runCatching {
        apiService.getMidTaForecast(regId)
    }

    suspend fun getMidLand(regId: String): Result<List<Map<String, String>>> = Result.runCatching {
        apiService.getMidLandForecast(regId)
    }
}