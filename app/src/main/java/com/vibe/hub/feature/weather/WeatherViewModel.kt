package com.vibe.hub.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibe.hub.data.repository.WeatherRepository
import com.vibe.hub.model.WeatherItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @HiltViewModel: 이 클래스가 Hilt를 통해 의존성을 주입받는 ViewModel임을 나타냅니다.
 * 
 * Spring의 @Service 또는 @Component 클래스가 비즈니스 로직을 처리하는 것과 비슷합니다.
 * Android에서는 화면 상태(State)를 보존하고 비즈니스 로직을 호출하는 역할을 합니다.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // UI 상태 정의 (Lce 패턴: Loading, Content, Error)
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
                    _uiState.value = WeatherUiState.Error(error.message ?: "알 수 없는 오류")
                }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: List<WeatherItem>) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
