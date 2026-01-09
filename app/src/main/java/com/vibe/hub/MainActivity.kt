package com.vibe.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vibe.hub.feature.home.HomeScreen
import com.vibe.hub.feature.home.WebViewScreen
import com.vibe.hub.feature.weather.WeatherScreen
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService
import com.vibe.hub.ui.theme.VibeHubTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            VibeHubTheme {
                VibeHubNavigation(fusedLocationClient)
            }
        }
    }
}

@Composable
fun VibeHubNavigation(fusedLocationClient: FusedLocationProviderClient) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // 권한 요청 후 이동할 데이터를 임시 보관할 상태
    var pendingService by remember { mutableStateOf<Pair<VibeService, LaunchMode>?>(null) }

    // 통합 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (granted) {
            // 권한 승인 시 대기 중인 서비스 실행
            pendingService?.let { (service, mode) ->
                navigateToService(navController, fusedLocationClient, context, service, mode)
            }
        } else {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
        pendingService = null
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onServiceClick = { service, mode ->
                    val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                    if (hasFineLocation || hasCoarseLocation) {
                        // 권한이 이미 있으면 즉시 이동
                        navigateToService(navController, fusedLocationClient, context, service, mode)
                    } else {
                        // 권한이 없으면 대기 상태로 두고 요청
                        pendingService = Pair(service, mode)
                        permissionLauncher.launch(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
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

/**
 * 서비스의 실행 모드에 따라 목적지로 네비게이션을 수행합니다.
 */
private fun navigateToService(
    navController: androidx.navigation.NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    context: android.content.Context,
    service: VibeService,
    mode: LaunchMode
) {
    if (mode == LaunchMode.WEBVIEW) {
        val encodedUrl = URLEncoder.encode(service.webUrl, StandardCharsets.UTF_8.toString())
        navController.navigate("webview/${encodedUrl}/${service.name}")
    } else {
        // 네이티브 모드: 실제 위치 좌표 획득 시도
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 37.5665
                val lon = location?.longitude ?: 126.9780
                navController.navigate("weather/${lat.toFloat()}/${lon.toFloat()}")
            }
        } catch (e: SecurityException) {
            // 권한이 없는 경우 (이론상 여기에 도달하면 안 됨)
            navController.navigate("weather/37.5665/126.9780")
        }
    }
}