package com.vibe.hub.data.api

import com.vibe.hub.model.WeatherItem
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 기상청 데이터를 제공하는 Vibe Weather 서버와 통신하는 인터페이스입니다.
 * Spring Boot의 @Controller와 통신하는 클라이언트 역할을 합니다.
 */
interface WeatherApiService {
    /**
     * 위도(lat)와 경도(lon)를 쿼리 파라미터로 받아 날씨 예보 목록을 가져옵니다.
     * Spring의 @GetMapping("/api/weather") 엔드포인트와 매핑됩니다.
     */
    @GET("/api/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherItem>
}