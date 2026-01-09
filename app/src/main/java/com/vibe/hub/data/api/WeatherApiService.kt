package com.vibe.hub.data.api

import com.vibe.hub.model.WeatherItem
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    /**
     * @GET: HTTP GET 요청을 정의합니다.
     * Spring의 @GetMapping과 같습니다.
     */
    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>
}
