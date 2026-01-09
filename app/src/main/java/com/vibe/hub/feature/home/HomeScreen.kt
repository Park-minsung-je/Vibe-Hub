package com.vibe.hub.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (VibeService, LaunchMode) -> Unit
) {
    val services = listOf(
        VibeService(
            id = "weather",
            name = "Vibe Weather",
            description = "실시간 위치 기반 날씨",
            iconUrl = "",
            webUrl = "https://vibe.weather.ilf.kr"
        ),
        // 향후 추가될 서비스들을 위한 공간
    )

    // 배경 그라데이션
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(VibeBlue.copy(alpha = 0.1f), Color.White)
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Vibe Hub",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                        Text(
                            "당신의 모든 바이브를 한곳에서",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(services) { service ->
                    VibeServiceCard(
                        service = service,
                        onClick = onServiceClick
                    )
                }
            }
        }
    }
}