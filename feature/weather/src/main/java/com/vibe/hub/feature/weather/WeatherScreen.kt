package com.vibe.hub.feature.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.core.ui.VibeBlue
import com.vibe.hub.core.ui.VibePurple

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
    val scrollState = rememberLazyListState()

    // 스크롤 위치에 따른 알파값 계산 (0.0 ~ 1.0)
    // 첫 번째 아이템이 사라질 때까지 투명도를 조절합니다.
    val toolbarAlpha by animateFloatAsState(
        targetValue = if (scrollState.firstVisibleItemIndex > 0) 0f else 1f,
        label = "ToolbarAlpha"
    )

    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lat, lon)
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F2F1), Color(0xFFF3E5F5))
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundBrush)
    ) {
        // 1. 메인 콘텐츠 (스크롤 가능한 리스트)
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VibePurple)
            }
            is WeatherUiState.Success -> {
                WeatherLuxuryContent(state.data, scrollState)
            }
            is WeatherUiState.Error -> {
                Text(text = "오류: ${state.message}", modifier = Modifier.align(Alignment.Center))
            }
        }

        // 2. 스크롤에 반응하는 상단바 및 뒤로가기 버튼
        WeatherCollapsingToolbar(
            alpha = toolbarAlpha,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun WeatherCollapsingToolbar(
    alpha: Float,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp)
    ) {
        // 타이틀 (스크롤 시 서서히 사라짐)
        Text(
            text = "Vibe Weather",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alpha),
            letterSpacing = (-1).sp
        )

        // 세련된 플로팅 스타일 뒤로가기 버튼 (항상 유지됨)
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart)
                .shadow(elevation = if (alpha < 1f) 8.dp else 0.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    if (alpha < 1f) {
                        // 스크롤되어 상단바가 사라지면 그라데이션 배경 적용
                        Brush.linearGradient(colors = listOf(VibeBlue, VibePurple))
                    } else {
                        // 초기 상태에서는 투명 배경
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                )
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = if (alpha < 1f) Color.White else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun WeatherLuxuryContent(items: List<WeatherItem>, scrollState: LazyListState) {
    val currentData = items.filter { it.fcstDate == items[0].fcstDate && it.fcstTime == items[0].fcstTime }
    val hourlyData = items.groupBy { "${it.fcstDate}${it.fcstTime}" }.values.toList()

    LazyColumn(
        state = scrollState, // 스크롤 상태 공유
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 80.dp, start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { LuxuryMainCard(currentData) }
        item {
            LuxurySectionTitle("시간별 예보")
            LuxuryHourlySection(hourlyData)
        }
        item {
            LuxurySectionTitle("상세 기상 정보")
            LuxuryDetailGrid(currentData)
        }
        item {
            LuxurySectionTitle("일자별 예보")
            LuxuryDailyList()
        }
    }
}

// ... LuxurySectionTitle, LuxuryMainCard, LuxuryHourlySection, LuxuryDetailGrid, LuxuryDailyList, getSkyState, getWeatherEmoji, getUnit 함수들 유지 ...

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
    val ptyValue = items.find { it.category == "PTY" }?.fcstValue ?: "0"
    
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
                Text(
                    text = getSkyState(skyValue, ptyValue), 
                    color = Color.White, 
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun LuxuryHourlySection(groupedItems: List<List<WeatherItem>>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(groupedItems) { timeGroup ->
            val time = timeGroup[0].fcstTime.substring(0, 2)
            val temp = timeGroup.find { it.category == "TMP" }?.fcstValue ?: ""
            val sky = timeGroup.find { it.category == "SKY" }?.fcstValue ?: "1"
            val pty = timeGroup.find { it.category == "PTY" }?.fcstValue ?: "0"
            
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${time}시", style = MaterialTheme.typography.labelMedium, color = VibePurple)
                Text(getWeatherEmoji(sky, pty), fontSize = 24.sp, modifier = Modifier.padding(vertical = 12.dp))
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
                            Text(value + getUnit(cat), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.DarkGray)
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
                    Text("1월 ${8 + i}일", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Text("☀️", modifier = Modifier.weight(1f))
                    Text("12° / 24°", fontWeight = FontWeight.Bold, color = VibeBlue)
                }
                if (i < 4) HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            }
        }
    }
}

fun getSkyState(sky: String, pty: String): String {
    val ptyMap = mapOf("1" to "비", "2" to "비/눈", "3" to "눈", "4" to "소나기", "5" to "빗방울", "6" to "빗방울눈날림", "7" to "눈날림")
    if (pty != "0" && ptyMap.containsKey(pty)) return ptyMap[pty]!!
    return when(sky) {
        "1" -> "맑음"
        "3" -> "구름 많음"
        "4" -> "흐림"
        else -> "알 수 없음"
    }
}

fun getWeatherEmoji(sky: String, pty: String): String {
    if (pty != "0") return when(pty) {
        "1", "4", "5" -> "🌧️"
        "2", "6" -> "🌨️"
        "3", "7" -> "❄️"
        else -> "🌦️"
    }
    return when(sky) {
        "1" -> "☀️"
        "3" -> "🌤️"
        "4" -> "☁️"
        else -> "☀️"
    }
}

fun getUnit(category: String): String = when(category) {
    "REH", "POP" -> "%"
    "WSD" -> "m/s"
    "VEC" -> "°"
    else -> ""
}