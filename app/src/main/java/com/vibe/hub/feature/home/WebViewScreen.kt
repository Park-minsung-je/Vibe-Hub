package com.vibe.hub.feature.home

import android.app.Activity
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit
) {
    val webBackgroundColor = Color(0xFFF0F8FF) // vibe-weather 배경색
    val view = LocalView.current

    /**
     * Android 15/16 대응: 
     * 화면 진입 시 시스템 바 아이콘 색상을 명시적으로 어둡게(Light Appearance) 설정합니다.
     */
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            
            // 이전 상태 보존은 생략하고 현재 화면에 필요한 스타일 강제 적용
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true

            onDispose {
                // 필요 시 화면을 벗어날 때 복구 로직을 넣을 수 있습니다.
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(webBackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 상단 상태바 영역 (웹 배경색과 일치)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(webBackgroundColor)
            )

            // 2. 웹뷰 본체 (스크롤 비침을 위해 하단 내비바 영역까지 weight를 주어 채움)
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(0) 
                            webViewClient = WebViewClient()
                            webChromeClient = object : WebChromeClient() {
                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    callback?.invoke(origin, true, false)
                                }
                            }
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                setGeolocationEnabled(true)
                                useWideViewPort = true
                                loadWithOverviewMode = true
                            }
                            loadUrl(url)
                        }
                    }
                )
            }

            // 3. 하단 내비게이션 바 영역 (스크롤 끝 가림 방지 및 웹 배경색 통일)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .background(webBackgroundColor)
            )
        }

        // 4. 플로팅 뒤로가기 버튼
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(40.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(VibeBlue, VibePurple)))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
