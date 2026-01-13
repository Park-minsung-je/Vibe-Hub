package com.vibe.hub.feature.weather

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    var cachedAddress: String? = null
    var cachedCurrent: List<WeatherItem>? = null
    var cachedHourly: List<WeatherItem>? = null
    var cachedMidTa: Map<String, String>? = null
    var cachedMidLand: Map<String, String>? = null
    var cachedAirQuality: AirQualityItem? = null // [추가] 대기질 캐시
    var cachedFetchTime: String? = null

    fun hasCache(): Boolean = cachedHourly != null && cachedCurrent != null

    fun clearCache() {
        cachedAddress = null
        cachedCurrent = null
        cachedHourly = null
        cachedMidTa = null
        cachedMidLand = null
        cachedAirQuality = null
        cachedFetchTime = null
    }

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

    // [추가] 대기질 정보 가져오기 (측정소 조회 -> 대기질 조회)
    suspend fun getAirQuality(lat: Double, lon: Double): Result<AirQualityItem?> = Result.runCatching {
        val stationRes = apiService.getNearestStation(lat, lon)
        val stationName = stationRes["stationName"]
        
        if (!stationName.isNullOrEmpty()) {
            val airRes = apiService.getAirQuality(stationName)
            airRes.firstOrNull()
        } else {
            null
        }
    }
}
