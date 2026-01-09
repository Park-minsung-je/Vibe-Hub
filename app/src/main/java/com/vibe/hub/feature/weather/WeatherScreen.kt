package com.vibe.hub.feature.weather

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
        colors = listOf(VibeBlue.copy(alpha = 0.05f), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vibe Weather", fontWeight = FontWeight.ExtraBold) },
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VibePurple)
                }
                is WeatherUiState.Success -> {
                    WeatherPolishedContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Text(text = "오류: ${state.message}", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun WeatherPolishedContent(items: List<WeatherItem>) {
    val currentData = items.filter { it.fcstDate == items[0].fcstDate && it.fcstTime == items[0].fcstTime }
    val hourlyData = items.groupBy { "${it.fcstDate}${it.fcstTime}" }.values.toList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. 현재 날씨 요약 (가장 시인성이 높은 디자인)
        item {
            CurrentWeatherCard(currentData)
        }

        // 2. 시간별 예보
        item {
            SectionTitle("시간별 예보")
            HourlyWeatherSection(hourlyData)
        }

        // 3. 상세 기상 정보 (그리드 카드)
        item {
            SectionTitle("상세 기상 정보")
            DetailedWeatherGrid(currentData)
        }

        // 4. 일자별 예보 (새로 추가)
        item {
            SectionTitle("일자별 예보 (준비 중)")
            DailyWeatherList()
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun CurrentWeatherCard(items: List<WeatherItem>) {
    val temp = items.find { it.category == "TMP" }?.fcstValue ?: "--"
    val skyValue = items.find { it.category == "SKY" }?.fcstValue ?: "1"
    val skyLabel = getSkyDescription(skyValue)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = VibeBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("현재 기온", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge)
            Text(text = "${temp}℃", fontSize = 72.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    text = skyLabel,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HourlyWeatherSection(groupedItems: List<List<WeatherItem>>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(groupedItems) { timeGroup ->
            val time = timeGroup[0].fcstTime.substring(0, 2)
            val temp = timeGroup.find { it.category == "TMP" }?.fcstValue ?: ""
            
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${time}시", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text("🌤️", fontSize = 28.sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text("${temp}°", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun DetailedWeatherGrid(items: List<WeatherItem>) {
    val categories = listOf("REH" to "습도", "WSD" to "풍속", "POP" to "강수확률", "VEC" to "풍향")
    
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { (cat, label) ->
                    val value = items.find { it.category == cat }?.fcstValue ?: "--"
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyWeatherList() {
    // 임시 일자별 예보 리스트
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            repeat(5) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("내일 + ${i}일", modifier = Modifier.weight(1f))
                    Text("☀️", modifier = Modifier.weight(1f))
                    Text("12° / 22°", fontWeight = FontWeight.Bold)
                }
                if (i < 4) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

fun getSkyDescription(value: String): String = when(value) {
    "1" -> "맑음"
    "3" -> "구름많음"
    "4" -> "흐림"
    else -> "맑음"
}
