package com.vibe.hub.feature.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.model.WeatherItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    onBackClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면 진입 시 API 호출
    LaunchedEffect(lat, lon) {
        viewModel.fetchWeather(lat, lon)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vibe Weather (Native)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("날씨 정보를 가져오는 중...")
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️ 오류 발생", style = MaterialTheme.typography.titleLarge)
                        Text(state.message)
                        Button(
                            onClick = { viewModel.fetchWeather(lat, lon) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("다시 시도")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(items: List<WeatherItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "실시간 기상 정보",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text("${item.category}") },
                    trailingContent = { Text("${item.fcstValue}", style = MaterialTheme.typography.titleMedium) },
                    supportingContent = { Text("예보 시간: ${item.fcstDate} ${item.fcstTime}") }
                )
            }
        }
    }
}