package com.vibe.hub.feature.weather

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vibe.hub.core.ui.VibeBlue
import com.vibe.hub.core.ui.VibePurple
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

    val topColor = Color(0xFFE0F2F1)
    val bottomColor = Color(0xFFF3E5F5)

    val toolbarHeight = 64.dp
    val toolbarHeightPx = with(density) { toolbarHeight.roundToPx().toFloat() }
    
    var isToolbarVisible by remember { mutableStateOf(true) }
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (isToolbarVisible) 0f else -toolbarHeightPx,
        animationSpec = tween(durationMillis = 350),
        label = "ToolbarOffset"
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1f) isToolbarVisible = false
                else if (available.y > 1f) isToolbarVisible = true
                return Offset.Zero
            }
        }
    }

    // Pull to Refresh 상태 관리
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchWeather(lat, lon)
        }
    }

    val backgroundBrush = Brush.verticalGradient(colors = listOf(topColor, bottomColor))

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.fetchWeather(lat, lon)
        },
        state = refreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .nestedScroll(nestedScrollConnection)
        ) {
            // 1. 메인 콘텐츠
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    if (!isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VibePurple)
                    }
                }
                is WeatherUiState.Success -> {
                    if (isRefreshing) isRefreshing = false
                    WeatherLuxuryContent(state.data, toolbarHeight)
                }
                is WeatherUiState.Error -> {
                    if (isRefreshing) isRefreshing = false
                    Text(text = "오류: ${state.message}", modifier = Modifier.align(Alignment.Center))
                }
            }

            // 2. 상단 상태바 영역 가림막
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(topColor)
                    .zIndex(10f)
            )

            // 3. 애니메이션 상단바 (타이틀)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(toolbarHeight)
                    .offset { IntOffset(x = 0, y = animatedOffset.roundToInt()) }
                    .background(topColor)
                    .zIndex(5f)
            ) {
                Text(
                    text = "Vibe Weather",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 64.dp),
                    color = Color.Black
                )
            }

            // 4. 뒤로가기 버튼 레이어 (Canvas 80dp 확장 버전 보존)
            val buttonProgress = 1f - (animatedOffset / -toolbarHeightPx)
            val iconColor by animateColorAsState(
                targetValue = if (buttonProgress < 0.5f) Color.White else Color.Black,
                label = "IconColor"
            )
            val bgScale by animateFloatAsState(if (buttonProgress < 0.5f) 1f else 0.8f, label = "BgScale")
            val bgAlpha by animateFloatAsState(if (buttonProgress < 0.2f) 1f else 0f, label = "BgAlpha")

            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(toolbarHeight)
                    .padding(start = 0.dp) 
                    .width(80.dp)
                    .zIndex(15f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(bgScale)
                        .alpha(bgAlpha)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = Paint()
                                val frameworkPaint = paint.asFrameworkPaint()
                                frameworkPaint.color = android.graphics.Color.BLACK
                                frameworkPaint.alpha = 50
                                frameworkPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
                                
                                val buttonRadius = 20.dp.toPx()
                                canvas.drawCircle(
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = buttonRadius,
                                    paint = paint
                                )
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(VibeBlue, VibePurple)))
                    )
                }
                
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp).align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 5. 하단 고정 가림막
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .background(bottomColor)
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
            )
        }
    }
}

@Composable
fun WeatherLuxuryContent(items: List<WeatherItem>, toolbarHeight: Dp) {
    val currentData = items.filter { it.fcstDate == items[0].fcstDate && it.fcstTime == items[0].fcstTime }
    val hourlyData = items.groupBy { "${it.fcstDate}${it.fcstTime}" }.values.toList()

    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + toolbarHeight + 16.dp,
            start = 20.dp, 
            end = 20.dp, 
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 100 } + scaleIn(tween(500), initialScale = 0.9f)
            ) {
                LuxuryMainCard(currentData)
            }
        }
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(tween(500, delayMillis = 100)) { 100 }
            ) {
                Column {
                    LuxurySectionTitle("시간별 예보")
                    LuxuryHourlySection(hourlyData)
                }
            }
        }
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(tween(500, delayMillis = 200)) { 100 }
            ) {
                Column {
                    LuxurySectionTitle("상세 기상 정보")
                    LuxuryDetailGrid(currentData)
                }
            }
        }
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, delayMillis = 300)) + slideInVertically(tween(500, delayMillis = 300)) { 100 }
            ) {
                Column {
                    LuxurySectionTitle("일자별 예보")
                    LuxuryDailyList()
                }
            }
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