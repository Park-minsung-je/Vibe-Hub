package com.vibe.hub.feature.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    // 주소 정보 가져오기
    @GET("/api/address")
    suspend fun getAddress(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Map<String, String>

    // 통합 현재 날씨 (실황 포함)
    @GET("/api/weather/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    // 시간별 예보
    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    // 중기 기온 예보 (3~10일)
    @GET("/api/midta_forecast")
    suspend fun getMidTaForecast(
        @Query("regId") regId: String
    ): List<Map<String, String>>

    // 중기 육상 예보 (3~10일 날씨상태)
    @GET("/api/midland_forecast")
    suspend fun getMidLandForecast(
        @Query("regId") regId: String
    ): List<Map<String, String>>
}
