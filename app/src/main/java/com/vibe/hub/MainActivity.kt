package com.vibe.hub

import android.os.Bundle
import android.widget.Toast
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
import com.vibe.hub.model.LaunchMode
import com.vibe.hub.ui.theme.VibeHubTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * @AndroidEntryPoint: Hilt에게 이 클래스(Activity)에 의존성을 주입해달라고 알리는 마커입니다.
 * 
 * Spring의 @Component나 @Controller가 붙은 클래스에 빈(Bean)이 주입되는 것과 비슷합니다.
 * 이 어노테이션이 있어야 Activity 내부에서 ViewModel 등을 주입(@Inject)받을 수 있습니다.
 */
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
        // 홈 화면
        composable("home") {
            HomeScreen(
                onServiceClick = { service, mode ->
                    if (mode == LaunchMode.WEBVIEW) {
                        // URL은 인코딩해서 전달해야 합니다.
                        val encodedUrl = URLEncoder.encode(service.webUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("webview/${encodedUrl}/${service.name}")
                    } else {
                        // 네이티브 모드는 나중에 구현 예정이므로 토스트만 띄움
                        // TODO: Native 화면 네비게이션 구현
                    }
                }
            )
        }

        // 웹뷰 화면 (URL과 제목을 인자로 받음)
        composable(
            route = "webview/{url}/{title}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: "Vibe Hub"
            
            WebViewScreen(
                url = url,
                title = title,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}