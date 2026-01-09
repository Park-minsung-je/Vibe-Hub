package com.vibe.hub.feature.home

import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit
) {
    // Scaffold 없이 Box를 사용하여 전체 화면을 구성합니다.
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 전체 화면 WebView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
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
                        
                        // 해상도 조정을 위해 최소한의 설정만 유지합니다.
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                    loadUrl(url)
                }
            }
        )

        // 2. 플로팅 뒤로가기 버튼 (좌측 상단)
        Surface(
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp) // 상태바 높이 고려
                .size(40.dp)
                .align(Alignment.TopStart),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.3f), // 반투명 배경
            contentColor = Color.White
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}