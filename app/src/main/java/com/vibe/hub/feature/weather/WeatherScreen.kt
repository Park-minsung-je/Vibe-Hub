package com.vibe.hub.feature.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    onBackClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel() // Hilt를 통해 ViewModel 주입
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 켜질 때 데이터 요청
    LaunchedEffect(lat, lon) {
        viewModel.fetchWeather(lat, lon)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Native Weather") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WeatherUiState.Success -> {
                    WeatherContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Text(
                        text = "오류 발생: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherContent(items: List<WeatherItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            ListItem(
                headlineContent = { Text("${item.category}: ${item.fcstValue}") },
                supportingContent = { Text("시간: ${item.fcstDate} ${item.fcstTime}") }
            )
            HorizontalDivider()
        }
    }
}
