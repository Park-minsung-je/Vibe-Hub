package com.vibe.hub.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            
            try {
                val currentDeferred = async { repository.getCurrentWeather(lat, lon) }
                val hourlyDeferred = async { repository.getHourlyForecast(lat, lon) }
                // 중기예보 구역코드 (일단 서울 기준으로 테스트)
                val midTaDeferred = async { repository.getMidTa("11B10101") }
                val midLandDeferred = async { repository.getMidLand("11B00000") }

                val current = currentDeferred.await().getOrDefault(emptyList())
                val hourly = hourlyDeferred.await().getOrDefault(emptyList())
                val midTa = midTaDeferred.await().getOrDefault(emptyList())
                val midLand = midLandDeferred.await().getOrDefault(emptyList())

                _uiState.value = WeatherUiState.Success(
                    current = current,
                    hourly = hourly,
                    midTa = midTa.firstOrNull() ?: emptyMap(),
                    midLand = midLand.firstOrNull() ?: emptyMap()
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "데이터 로드 실패")
            }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val current: List<WeatherItem>,
        val hourly: List<WeatherItem>,
        val midTa: Map<String, String>,
        val midLand: Map<String, String>
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
