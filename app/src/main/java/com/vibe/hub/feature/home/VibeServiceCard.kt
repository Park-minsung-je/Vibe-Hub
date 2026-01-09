package com.vibe.hub.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService

@Composable
fun VibeServiceCard(
    service: VibeService,
    onClick: (VibeService, LaunchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var launchMode by remember { mutableStateOf(if (service.isNativeSupported) LaunchMode.NATIVE else LaunchMode.WEBVIEW) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(service, launchMode) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Placeholder (Coil 등을 사용하여 이미지 로드 가능)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌤️", fontSize = 40.sp) // 임시 아이콘
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Native / Web Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Alignment.SpaceBetween
            ) {
                Text(
                    text = if (launchMode == LaunchMode.NATIVE) "Native" else "WebView",
                    style = MaterialTheme.typography.labelSmall
                )
                Switch(
                    checked = launchMode == LaunchMode.WEBVIEW,
                    onCheckedChange = { isWeb ->
                        launchMode = if (isWeb) LaunchMode.WEBVIEW else LaunchMode.NATIVE
                    },
                    enabled = service.isNativeSupported, // 네이티브 미지원시 비활성화
                    modifier = Modifier.scale(0.7f)
                )
            }
        }
    }
}

@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)
