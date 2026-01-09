package com.vibe.hub.data.repository

import com.vibe.hub.data.api.WeatherApiService
import com.vibe.hub.model.WeatherItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Inject constructor: 이 클래스를 생성할 때 필요한 의존성을 Hilt가 주입해줍니다.
 * Spring의 @Autowired가 생성자에 붙은 것과 같습니다.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {
    suspend fun getRemoteWeather(lat: Double, lon: Double): Result<List<WeatherItem>> {
        return try {
            val response = apiService.getWeatherData(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
