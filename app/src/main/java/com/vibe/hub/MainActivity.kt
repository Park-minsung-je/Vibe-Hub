package com.vibe.hub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vibe.hub.feature.home.HomeScreen
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.ui.theme.VibeHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibeHubTheme {
                HomeScreen(
                    onServiceClick = { service, mode ->
                        if (mode == LaunchMode.WEBVIEW) {
                            Toast.makeText(this, "${service.name} (WebView) 실행 예정: ${service.webUrl}", Toast.LENGTH_SHORT).show()
                            // TODO: WebView Activity 실행 로직
                        } else {
                            Toast.makeText(this, "${service.name} (Native) 실행 예정", Toast.LENGTH_SHORT).show()
                            // TODO: Native 화면 네비게이션 로직
                        }
                    }
                )
            }
        }
    }
}
