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
 * 날씨 화면의 UI 상태와 비즈니스 로직을 관리합니다.
 * Spring의 @Controller가 요청을 받아 서비스 계층을 호출하는 것과 유사하게,
 * UI(Compose)의 요청을 받아 레포지토리를 호출하고 그 결과를 관찰 가능한 상태(StateFlow)로 노출합니다.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    /**
     * UI 상태를 담는 컨테이너입니다.
     * 초기값은 로딩 중(Loading) 상태입니다.
     */
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    /**
     * 특정 좌표의 날씨 데이터를 요청합니다.
     * 코루틴(launch)을 사용하여 비동기로 통신을 수행합니다.
     */
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

/**
 * 화면의 다양한 상태를 표현하는 Sealed Class입니다.
 * Loading: 로딩 중, Success: 성공 및 데이터 전달, Error: 실패 및 메시지 전달
 */
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: List<WeatherItem>) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}