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

    private var isDataLoaded = false

    fun fetchWeather(lat: Double, lon: Double, forceRefresh: Boolean = false) {
        if (isDataLoaded && !forceRefresh) return

        viewModelScope.launch {
            // 새로고침 시에도 Loading 상태를 전송하여 UI와 LaunchedEffect가 정상 동작하게 함
            _uiState.value = WeatherUiState.Loading
            
            try {
                val currentDeferred = async { repository.getCurrentWeather(lat, lon) }
                val hourlyDeferred = async { repository.getHourlyForecast(lat, lon) }
                val midTaDeferred = async { repository.getMidTa("11B10101") }
                val midLandDeferred = async { repository.getMidLand("11B00000") }

                val current = currentDeferred.await().getOrDefault(emptyList())
                val hourly = hourlyDeferred.await().getOrDefault(emptyList())
                val midTa = midTaDeferred.await().getOrDefault(emptyList())
                val midLand = midLandDeferred.await().getOrDefault(emptyList())

                if (hourly.isNotEmpty() || current.isNotEmpty()) {
                    _uiState.value = WeatherUiState.Success(
                        current = current,
                        hourly = hourly,
                        midTa = midTa.firstOrNull() ?: emptyMap(),
                        midLand = midLand.firstOrNull() ?: emptyMap()
                    )
                    isDataLoaded = true
                } else {
                    _uiState.value = WeatherUiState.Error("날씨 데이터를 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "네트워크 오류가 발생했습니다.")
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
