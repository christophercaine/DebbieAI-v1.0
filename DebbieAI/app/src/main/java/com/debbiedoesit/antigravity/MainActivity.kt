package com.debbiedoesit.antigravity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.debbiedoesit.antigravity.ui.ChatScreen
import com.debbiedoesit.antigravity.ui.ContactsScreen
import com.debbiedoesit.antigravity.ui.FeaturePlaceholder
import com.debbiedoesit.antigravity.ui.HomeScreen
import com.debbiedoesit.antigravity.ui.JobSitePhotoScreen
import com.debbiedoesit.antigravity.ui.MarketingScreen
import com.debbiedoesit.antigravity.ui.MeasurementScreen
import com.debbiedoesit.antigravity.ui.VideoToolsScreen
import com.debbiedoesit.antigravity.ui.theme.AntigravityTheme
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)

        val tier = DeviceCapabilityDetector.detect(this)

        setContent {
            AntigravityTheme {
                // Request permissions on startup
                PermissionWrapper {
                    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                        val navController = rememberNavController()
                        var isAuthenticated by remember {
                            mutableStateOf(false)
                        } // Placeholder for actual auth state

                        NavHost(
                                navController = navController,
                                startDestination = if (isAuthenticated) "home" else "login"
                        ) {
                            composable("login") {
                                LoginScreen(onLoginSuccess = { isAuthenticated = true })
                            }
                            composable("home") {
                                HomeScreen(tier) { route -> navController.navigate(route) }
                            }
                            composable("chat") {
                                ChatScreen(onBack = { navController.popBackStack() })
                            }
                            composable("measure") {
                                MeasurementScreen(onBack = { navController.popBackStack() })
                            }
                            composable("photos") {
                                JobSitePhotoScreen(onBack = { navController.popBackStack() })
                            }
                            composable("video") {
                                VideoToolsScreen(onBack = { navController.popBackStack() })
                            }
                            composable("contacts") {
                                ContactsScreen(onBack = { navController.popBackStack() })
                            }
                            composable("marketing") {
                                MarketingScreen(onBack = { navController.popBackStack() })
                            }
                            composable("floor_plan") { FeaturePlaceholder("Floor Plans") }
                            composable("details") { FeaturePlaceholder("Details") }
                            composable("data") { FeaturePlaceholder("Data & Insights") }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionWrapper(content: @Composable () -> Unit) {
    val permissions =
            mutableListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(Unit) { permissionState.launchMultiplePermissionRequest() }

    if (permissionState.allPermissionsGranted) {
        content()
    } else {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                        "Permissions Required",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        "DebbieAI needs camera, audio, and storage permissions to function correctly.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // Simplified Login placeholder for now
    Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
    ) {
        Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.35f), // Roughly top 1/3
                    contentAlignment = Alignment.Center
            ) {
                Image(
                        painter = painterResource(id = R.drawable.debbieai_logo),
                        contentDescription = "Debbie Logo",
                        modifier = Modifier.fillMaxSize(0.8f).clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    "DebbieAI",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
            )
            Text(
                    "Your AI Field Partner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF00BCD4)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                    onClick = onLoginSuccess,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Login, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Login with Google", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                    onClick = onLoginSuccess,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
            ) { Text("Register with Email", color = Color.White) }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                    "By continuing, you agree to the Terms of Service",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
            )
        }
    }
}
