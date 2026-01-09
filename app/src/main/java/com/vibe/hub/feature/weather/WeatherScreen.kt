package com.vibe.hub.feature.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.model.WeatherItem
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    onBackClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.fetchWeather(lat, lon)
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(VibeBlue.copy(alpha = 0.2f), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("실시간 날씨", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(padding)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VibePurple)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("기상 정보를 불러오는 중...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Text(
                        text = "오류: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherContent(items: List<WeatherItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "오늘의 예보 정보",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(items) { item ->
            WeatherCard(item)
        }
    }
}

@Composable
fun WeatherCard(item: WeatherItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = VibeBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${item.fcstDate.substring(4, 6)}/${item.fcstDate.substring(6, 8)} ${item.fcstTime.substring(0, 2)}:00",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = item.fcstValue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
