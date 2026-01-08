package com.vibe.hub.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onServiceClick: (VibeService, LaunchMode) -> Unit
) {
    // 임시 데이터 (나중에 ViewModel/Repository로 분리)
    val services = listOf(
        VibeService(
            id = "weather",
            name = "Vibe Weather",
            description = "실시간 위치 기반 날씨",
            iconUrl = "",
            webUrl = "https://vibe.weather.ilf.kr"
        ),
        // 추가 서비스들...
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vibe Hub", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 한 줄에 2개
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
