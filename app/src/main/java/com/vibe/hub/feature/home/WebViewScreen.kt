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

    // 최상위 Box: 플로팅 버튼과 메인 콘텐츠를 겹치기 위함
    Box(modifier = Modifier.fillMaxSize().background(webBackgroundColor)) {
        
        // 메인 콘텐츠 Column
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 상단 상태바 영역 (웹 배경색과 일치)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(webBackgroundColor)
            )

            // 2. 웹뷰 본체 (스크롤 비침을 위해 하단 내비바 영역까지 꽉 채움)
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

            // 3. 하단 내비게이션 바 여백 (스크롤 시 내용이 가려지지 않게 함)
            // 웹뷰는 자체 스크롤이므로, 이 Spacer는 웹뷰 아래에 고정되어 
            // 웹뷰 콘텐츠가 이 영역 위까지만 나타나게 합니다.
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .background(webBackgroundColor) // 내비바 배경도 웹 배경색과 통일
            )
        }

        // 4. 플로팅 뒤로가기 버튼
        Box(
            modifier = Modifier
                .statusBarsPadding() // 버튼이 상태바 아래에 오도록 함
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