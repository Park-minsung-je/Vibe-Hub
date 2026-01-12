package com.vibe.hub.feature.weather

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 기상청 데이터를 제공하는 Vibe Weather 서버와 통신하는 인터페이스입니다.
 */
interface WeatherApiService {
    /**
     * 위도(lat)와 경도(lon)를 기반으로 날씨 데이터를 가져옵니다.
     * 서버 엔드포인트: /api/weather
     */
    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>

    // 중기 기온 예보 (추후 구현)
    @GET("/api/midta_forecast")
    suspend fun getMidTaForecast(
        @Query("regId") regId: String
    ): List<Map<String, Any>>

    // 중기 육상 예보 (추후 구현)
    @GET("/api/midland_forecast")
    suspend fun getMidLandForecast(
        @Query("regId") regId: String
    ): List<Map<String, Any>>
}
