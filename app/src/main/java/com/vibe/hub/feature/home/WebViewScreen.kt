package com.vibe.hub.feature.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vibe.hub.ui.theme.VibeBlue
import com.vibe.hub.ui.theme.VibePurple

@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit
) {
    val webBackgroundColor = Color(0xFFF0F8FF) // vibe-weather 배경색

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(webBackgroundColor) // 전체 배경색을 웹색상으로 설정
    ) {
        // 1. 상단 상태바 영역 박스 (웹 배경색과 일치시켜 끊김 없이 보이게 함)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(webBackgroundColor)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간 모두 차지
        ) {
            // 2. 웹뷰 본체 (하단 내비바 영역을 가리지 않도록 padding 추가)
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars), // 하단 내비바 패딩 추가
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // 웹뷰 자체의 배경색도 투명하게 하여 앱의 배경색이 보이도록 함
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

            // 3. 플로팅 뒤로가기 버튼
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .align(Alignment.TopStart)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(VibeBlue, VibePurple)
                        )
                    )
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
}