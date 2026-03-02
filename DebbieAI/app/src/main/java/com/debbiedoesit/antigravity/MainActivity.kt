package com.debbiedoesit.antigravity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.debbiedoesit.antigravity.ui.ChatScreen
import com.debbiedoesit.antigravity.ui.FeaturePlaceholder
import com.debbiedoesit.antigravity.ui.HomeScreen
import com.debbiedoesit.antigravity.ui.theme.AntigravityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        val tier = DeviceCapabilityDetector.detect(this)

        setContent {
            AntigravityTheme {
                Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(tier) { route -> navController.navigate(route) }
                        }
                        composable("chat") { ChatScreen() }
                        composable("measure") { FeaturePlaceholder("Measurements") }
                        composable("photos") { FeaturePlaceholder("Job Photos") }
                        composable("video") { FeaturePlaceholder("Video Tools") }
                        composable("marketing") { FeaturePlaceholder("Marketing") }
                        composable("floor_plan") { FeaturePlaceholder("Floor Plans") }
                        composable("contacts") { FeaturePlaceholder("Contacts") }
                        composable("details") { FeaturePlaceholder("Details") }
                        composable("data") { FeaturePlaceholder("Data & Insights") }
                    }
                }
            }
        }
    }
}
