package com.vibe.hub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vibe.hub.feature.home.HomeScreen
import com.vibe.hub.feature.home.WebViewScreen
import com.vibe.hub.feature.weather.WeatherScreen
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.ui.theme.VibeHubTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibeHubTheme {
                VibeHubNavigation()
            }
        }
    }
}

@Composable
fun VibeHubNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onServiceClick = { service, mode ->
                    if (mode == LaunchMode.WEBVIEW) {
                        val encodedUrl = URLEncoder.encode(service.webUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("webview/${encodedUrl}/${service.name}")
                    } else {
                        // 네이티브 날씨 화면으로 이동 (현재는 임시 좌표 37.5, 127.0 사용)
                        navController.navigate("weather/37.5/127.0")
                    }
                }
            )
        }

        composable(
            route = "webview/{url}/{title}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: "Vibe Hub"
            WebViewScreen(url = url, title = title, onBackClick = { navController.popBackStack() })
        }

        // 네이티브 날씨 화면 라우트 추가
        composable(
            route = "weather/{lat}/{lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            WeatherScreen(lat = lat, lon = lon, onBackClick = { navController.popBackStack() })
        }
    }
}
