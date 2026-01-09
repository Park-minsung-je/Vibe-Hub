package com.vibe.hub.feature.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.core.ui.VibeBlue
import com.vibe.hub.core.ui.VibePurple
import com.vibe.hub.model.WeatherItem
import kotlin.math.roundToInt

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
    val density = LocalDensity.current

    // 상단바 높이 설정
    val toolbarHeight = 64.dp
    val toolbarHeightPx = with(density) { toolbarHeight.roundToPx().toFloat() }
    
    // 상단바의 현재 오프셋 상태 (0: 완전 노출, -toolbarHeightPx: 완전 숨김)
    var toolbarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    // 스크롤 감지를 위한 NestedScrollConnection
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lat, lon)
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0F2F1), Color(0xFFF3E5F5))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .nestedScroll(nestedScrollConnection) // 스크롤 이벤트 연결
    ) {
        // 1. 메인 콘텐츠
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

        // 2. 이동하는 상단바 (배경 및 타이틀)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()) },
            color = Color.Transparent // 배경색은 투명하게 하여 일체감 유지
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 64.dp), // 뒤로가기 버튼 공간 확보
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Vibe Weather",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    letterSpacing = (-1).sp,
                    color = Color.Black
                )
            }
        }

        // 3. 고정된 위치의 뒤로가기 버튼 (상태에 따라 플로팅으로 변함)
        val progress = 1f - (toolbarOffsetHeightPx / -toolbarHeightPx) // 1.0(보임) -> 0.0(숨겨짐)
        
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .height(toolbarHeight)
                .padding(start = 12.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = if (progress < 0.8f) 8.dp else 0.dp, 
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(
                        if (progress < 0.8f) {
                            Brush.linearGradient(colors = listOf(VibeBlue, VibePurple))
                        } else {
                            Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = if (progress < 0.8f) Color.White else Color.Black,
                    modifier = Modifier.size(24.dp)
                )
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

// --- 하위 Composable 및 Helper 함수들 (기존과 동일) ---

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
