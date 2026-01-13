package com.vibe.hub.feature.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/api/address")
    suspend fun getAddress(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Map<String, String>

    @GET("/api/weather/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    @GET("/api/midta_forecast")
    suspend fun getMidTaForecast(
        @Query("regId") regId: String
    ): List<Map<String, String>>

    @GET("/api/midland_forecast")
    suspend fun getMidLandForecast(
        @Query("regId") regId: String
    ): List<Map<String, String>>

    // [추가] 가까운 측정소 찾기
    @GET("/api/station")
    suspend fun getNearestStation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Map<String, String>

    // [추가] 대기질 정보 조회
    @GET("/api/airquality")
    suspend fun getAirQuality(
        @Query("stationName") stationName: String
    ): List<AirQualityItem>
}