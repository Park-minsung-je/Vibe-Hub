package com.vibe.hub.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
            // 로딩 상태 전송은 이미 데이터가 있을 경우 생략할 수도 있지만,
            // Pull-to-Refresh에서 직접 제어하므로 여기서는 단순 호출만 담당해도 됨.
            // 하지만 초기 진입 시 로딩 표시를 위해 필요함.
            _uiState.value = WeatherUiState.Loading
            
            repository.getRemoteWeather(lat, lon)
                .onSuccess { items ->
                    if (items.isNotEmpty()) {
                        _uiState.value = WeatherUiState.Success(items)
                    } else {
                        _uiState.value = WeatherUiState.Error("날씨 데이터가 없습니다.")
                    }
                }
                .onFailure { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "날씨 데이터를 가져오는데 실패했습니다.")
                }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: List<WeatherItem>) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}