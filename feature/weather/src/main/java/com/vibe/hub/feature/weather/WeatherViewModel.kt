package com.vibe.hub.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val address: String,
        val fetchTime: String,
        val current: List<WeatherItem>,
        val hourly: List<WeatherItem>,
        val midTa: Map<String, String>,
        val midLand: Map<String, String>,
        val airQuality: AirQualityItem?, // [추가]
        val lastUpdated: Long
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun fetchWeather(lat: Double, lon: Double, forceRefresh: Boolean = false) {
        if (!forceRefresh && repository.hasCache()) {
            _uiState.value = WeatherUiState.Success(
                address = repository.cachedAddress ?: "주소 정보 없음",
                fetchTime = repository.cachedFetchTime ?: "",
                current = repository.cachedCurrent ?: emptyList(),
                hourly = repository.cachedHourly ?: emptyList(),
                midTa = repository.cachedMidTa ?: emptyMap(),
                midLand = repository.cachedMidLand ?: emptyMap(),
                airQuality = repository.cachedAirQuality,
                lastUpdated = System.currentTimeMillis()
            )
            return
        }

        viewModelScope.launch {
            if (!forceRefresh) _uiState.value = WeatherUiState.Loading
            
            try {
                if (forceRefresh) repository.clearCache()

                val addressDef = async { repository.getAddress(lat, lon) }
                val currentDef = async { repository.getCurrentWeather(lat, lon) }
                val hourlyDef = async { repository.getHourlyForecast(lat, lon) }
                val midTaDef = async { repository.getMidTa("11B10101") }
                val midLandDef = async { repository.getMidLand("11B00000") }
                val airQualityDef = async { repository.getAirQuality(lat, lon) } // [추가]

                val address = addressDef.await().getOrNull()?.get("address") ?: "주소 정보 없음"
                val current = currentDef.await().getOrDefault(emptyList())
                val hourly = hourlyDef.await().getOrDefault(emptyList())
                val midTa = midTaDef.await().getOrNull()?.firstOrNull() ?: emptyMap()
                val midLand = midLandDef.await().getOrNull()?.firstOrNull() ?: emptyMap()
                val airQuality = airQualityDef.await().getOrNull()

                val fetchTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

                if (hourly.isNotEmpty() || current.isNotEmpty()) {
                    repository.cachedAddress = address
                    repository.cachedFetchTime = fetchTime
                    repository.cachedCurrent = current
                    repository.cachedHourly = hourly
                    repository.cachedMidTa = midTa
                    repository.cachedMidLand = midLand
                    repository.cachedAirQuality = airQuality
                    
                    _uiState.value = WeatherUiState.Success(address, fetchTime, current, hourly, midTa, midLand, airQuality, System.currentTimeMillis())
                } else {
                    _uiState.value = WeatherUiState.Error("데이터 로드 실패")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "네트워크 에러")
            }
        }
    }
}