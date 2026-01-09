package com.vibe.hub.feature.weather

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.model.WeatherItem
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    onBackClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            // 권한 승인 시 데이터 요청 (실제로는 현재 위치를 가져오는 로직이 필요하지만 우선 전달받은 lat, lon 사용)
            viewModel.fetchWeather(lat, lon)
        } else {
            // 거부 시 처리 로직 (나중에 보강)
        }
    }

    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED || coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lat, lon)
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // 세련된 배경 그라데이션 (더 깊이 있는 색감)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0F2F1), // 상단 연한 민트/블루
            Color(0xFFF3E5F5)  // 하단 연한 퍼플
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vibe Weather", fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
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
                    WeatherLuxuryContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Text(text = "오류: ${state.message}", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun WeatherLuxuryContent(items: List<WeatherItem>) {
    val currentData = items.filter { it.fcstDate == items[0].fcstDate && it.fcstTime == items[0].fcstTime }
    val hourlyData = items.groupBy { "${it.fcstDate}${it.fcstTime}" }.values.toList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 메인 요약 카드 (Vibe 그라데이션 적용)
        item {
            LuxuryMainCard(currentData)
        }

        // 시간별 예보 (유리창 효과 카드)
        item {
            LuxurySectionTitle("시간별 예보")
            LuxuryHourlySection(hourlyData)
        }

        // 상세 정보 (깔끔한 아이콘 리스트)
        item {
            LuxurySectionTitle("상세 기상 정보")
            LuxuryDetailGrid(currentData)
        }

        // 일자별 예보 (모던 리스트)
        item {
            LuxurySectionTitle("일자별 예보 (10일)")
            LuxuryDailyList()
        }
    }
}

@Composable
fun LuxurySectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = VibePurple.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun LuxuryMainCard(items: List<WeatherItem>) {
    val temp = items.find { it.category == "TMP" }?.fcstValue ?: "--"
    val skyValue = items.find { it.category == "SKY" }?.fcstValue ?: "1"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(VibeBlue, VibePurple)))
                .padding(32.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("현재 기온", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text(text = "${temp}°", fontSize = 80.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text(text = getSkyDescription(skyValue), color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun LuxuryHourlySection(groupedItems: List<List<WeatherItem>>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(groupedItems) { timeGroup ->
            val time = timeGroup[0].fcstTime.substring(0, 2)
            val temp = timeGroup.find { it.category == "TMP" }?.fcstValue ?: ""
            
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${time}시", style = MaterialTheme.typography.labelMedium, color = VibePurple)
                Text("🌤️", fontSize = 24.sp, modifier = Modifier.padding(vertical = 12.dp))
                Text("${temp}°", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LuxuryDetailGrid(items: List<WeatherItem>) {
    val details = listOf("REH" to "습도", "WSD" to "풍속", "POP" to "강수확률", "VEC" to "풍향")
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        details.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { (cat, label) ->
                    val value = items.find { it.category == cat }?.fcstValue ?: "--"
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryDailyList() {
    Surface(
        color = Color.White.copy(alpha = 0.4f),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            repeat(5) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Day ${i + 1}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Text("☀️", modifier = Modifier.weight(1f))
                    Text("12° / 24°", fontWeight = FontWeight.Bold, color = VibeBlue)
                }
                if (i < 4) HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            }
        }
    }
}

fun getSkyDescription(value: String): String = when(value) {
    "1" -> "맑음"
    "3" -> "구름 많음"
    "4" -> "흐림"
    else -> "맑음"
}
