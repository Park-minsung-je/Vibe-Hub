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
import com.vibe.hub.core.ui.VibeBlue
import com.vibe.hub.core.ui.VibePurple

@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit
) {
    val webBackgroundColor = Color(0xFFF0F8FF)

    Box(modifier = Modifier.fillMaxSize().background(webBackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(webBackgroundColor)
            )

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

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .background(webBackgroundColor)
            )
        }

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