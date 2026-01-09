package com.vibe.hub.data.repository

import com.vibe.hub.data.api.WeatherApiService
import com.vibe.hub.model.WeatherItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 데이터 소스(여기서는 Remote API)로부터 데이터를 가져오는 역할을 담당합니다.
 * Spring의 @Repository 또는 @Service 계층과 유사하며, 비즈니스 로직의 데이터 관문을 추상화합니다.
 * 
 * @Inject constructor: Hilt가 생성자 주입을 통해 WeatherApiService 빈(Bean)을 자동으로 넣어줍니다.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    /**
     * 서버로부터 날씨 데이터를 가져와서 Result 객체로 감싸 반환합니다.
     * 예외 발생 시 에러 상태를 안전하게 전달합니다.
     */
    suspend fun getRemoteWeather(lat: Double, lon: Double): Result<List<WeatherItem>> {
        return try {
            val response = apiService.getWeatherData(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}