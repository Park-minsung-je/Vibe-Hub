package com.vibe.hub.data.api

import com.vibe.hub.model.WeatherItem
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    // 중기 기온 예보
    @GET("/api/midta_forecast")
    suspend fun getMidTaForecast(
        @Query("regId") regId: String
    ): List<Map<String, Any>> // 간단한 구현을 위해 맵으로 우선 받음

    // 중기 육상 예보
    @GET("/api/midland_forecast")
    suspend fun getMidLandForecast(
        @Query("regId") regId: String
    ): List<Map<String, Any>>
}
