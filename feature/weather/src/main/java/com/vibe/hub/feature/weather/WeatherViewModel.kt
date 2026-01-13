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

    // 메모리 내 데이터 로드 여부 플래그
    private var isDataLoaded = false

    /**
     * 날씨 데이터를 가져옵니다.
     * @param forceRefresh true일 경우 캐시를 무시하고 무조건 새로 가져옵니다.
     */
    fun fetchWeather(lat: Double, lon: Double, forceRefresh: Boolean = false) {
        // 이미 로드된 데이터가 있고, 강제 새로고침이 아니라면 그대로 유지
        if (isDataLoaded && !forceRefresh) return

        viewModelScope.launch {
            // 강제 새로고침이 아닐 때만 로딩 상태로 변경 (화면 깜빡임 방지)
            if (!forceRefresh) {
                _uiState.value = WeatherUiState.Loading
            }
            
            try {
                // API 병렬 호출
                val currentDeferred = async { repository.getCurrentWeather(lat, lon) }
                val hourlyDeferred = async { repository.getHourlyForecast(lat, lon) }
                // 중기예보 (일단 서울 고정, 추후 좌표 기반 regId 매핑 필요)
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