package com.vibe.hub.feature.home

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // AndroidView: Compose 내에서 기존 안드로이드 View 시스템의 위젯을 사용할 때 쓰는 래퍼입니다.
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient() // 외부 브라우저가 아닌 앱 내 웹뷰에서 페이지가 열리도록 함
                    settings.apply {
                        javaScriptEnabled = true // 자바스크립트 허용
                        domStorageEnabled = true // 로컬 스토리지 허용
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                // URL이 변경되거나 업데이트가 필요할 때 호출되는 영역입니다.
                // 이미 factory에서 loadUrl을 했으므로 별도 처리는 생략합니다.
            }
        )
    }
}
