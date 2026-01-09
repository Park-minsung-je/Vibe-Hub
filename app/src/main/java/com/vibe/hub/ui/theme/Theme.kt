package com.vibe.hub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VibeBlue,
    secondary = VibePurple,
    tertiary = VibePink
)

private val LightColorScheme = lightColorScheme(
    primary = VibeBlue,
    secondary = VibePurple,
    tertiary = VibePink
)

@Composable
fun VibeHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 앱 전체에서 밝은 테마의 아이콘(어두운색)을 사용하도록 유도
    // (다크 모드 지원 시 분기 가능하지만 현재는 밝은 배경 위주이므로 고정 또는 다크모드 대응)
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 배경은 투명하게 (enableEdgeToEdge와 연동)
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            // 배경이 밝으므로 아이콘을 어둡게 설정 (Light Theme Appearance)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}