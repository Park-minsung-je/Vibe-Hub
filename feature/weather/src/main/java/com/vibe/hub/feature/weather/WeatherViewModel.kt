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
            _uiState.value = WeatherUiState.Loading
            repository.getRemoteWeather(lat, lon)
                .onSuccess { items ->
                    _uiState.value = WeatherUiState.Success(items)
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
