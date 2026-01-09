package com.vibe.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vibe.hub.core.ui.VibeHubTheme
import com.vibe.hub.feature.home.HomeScreen
import com.vibe.hub.feature.home.WebViewScreen
import com.vibe.hub.feature.weather.WeatherScreen
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.model.VibeService
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 내비게이션 바는 기본 스타일로 유지
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        )
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
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
    
    var pendingService by remember { mutableStateOf<Pair<VibeService, LaunchMode>?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (granted) {
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
                        navigateToService(navController, fusedLocationClient, context, service, mode)
                    } else {
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
            route = "webview/{url}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(url = url, onBackClick = { navController.popBackStack() })
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

private fun navigateToService(
    navController: androidx.navigation.NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    context: android.content.Context,
    service: VibeService,
    mode: LaunchMode
) {
    if (mode == LaunchMode.WEBVIEW) {
        val encodedUrl = URLEncoder.encode(service.webUrl, StandardCharsets.UTF_8.toString())
        navController.navigate("webview/${encodedUrl}")
    } else {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 37.5665
                val lon = location?.longitude ?: 126.9780
                navController.navigate("weather/${lat.toFloat()}/${lon.toFloat()}")
            }
        } catch (e: SecurityException) {
            navController.navigate("weather/37.5665/126.9780")
        }
    }
}
