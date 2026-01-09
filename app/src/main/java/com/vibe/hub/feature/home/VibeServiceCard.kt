package com.vibe.hub.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

@Composable
fun VibeServiceCard(
    service: VibeService,
    onClick: (VibeService, LaunchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var launchMode by remember { mutableStateOf(if (service.isNativeSupported) LaunchMode.NATIVE else LaunchMode.WEBVIEW) }

    // 카드 배경 그라데이션
    val cardGradient = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick(service, launchMode) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .background(cardGradient)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 아이콘 영역 (원형 배경)
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(VibeBlue.copy(alpha = 0.2f), VibePurple.copy(alpha = 0.2f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌤️", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 모드 선택 영역 (고급스러운 디자인)
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (launchMode == LaunchMode.NATIVE) "Native" else "WebView",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Switch(
                        checked = launchMode == LaunchMode.WEBVIEW,
                        onCheckedChange = { isWeb ->
                            launchMode = if (isWeb) LaunchMode.WEBVIEW else LaunchMode.NATIVE
                        },
                        enabled = service.isNativeSupported,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VibePurple,
                            checkedTrackColor = VibePurple.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}
