package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debbiedoesit.antigravity.R
import com.debbiedoesit.antigravity.viewmodel.MeasurementViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MeasurementScreen(viewModel: MeasurementViewModel = viewModel(), onBack: () -> Unit = {}) {
        var refPixels by remember { mutableStateOf("") }
        var refInches by remember { mutableStateOf("") }
        var targetPixels by remember { mutableStateOf("") }
        var showManualMode by remember { mutableStateOf(!viewModel.isArSupported) }

        val result by viewModel.result.collectAsState()
        val arDistance by viewModel.arDistance.collectAsState()

        // Accompanist Camera Permission
        val cameraPermissionState =
                com.google.accompanist.permissions.rememberPermissionState(
                        android.Manifest.permission.CAMERA
                )

        LaunchedEffect(Unit) {
                if (!cameraPermissionState.status.isGranted) {
                        cameraPermissionState.launchPermissionRequest()
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("AI Measurements", color = Color.White) },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                actions = {
                                        if (viewModel.isArSupported) {
                                                TextButton(
                                                        onClick = {
                                                                showManualMode = !showManualMode
                                                        }
                                                ) {
                                                        Text(
                                                                if (showManualMode) "AR Mode"
                                                                else "Manual",
                                                                color = Color(0xFF4CAF50)
                                                        )
                                                }
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color(0xFF1A1A2E)
                                        )
                        )
                },
                containerColor = Color(0xFF121212)
        ) { padding ->
                if (showManualMode) {
                        // Manual Mode Form
                        Column(
                                modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Image(
                                        painter =
                                                painterResource(
                                                        id = R.drawable.ic_measurements_new
                                                ),
                                        contentDescription = "Debbie Does Measurements",
                                        modifier = Modifier.fillMaxWidth().height(120.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )

                                Text(
                                        "Reference Measurement Tool",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color(0xFF1E1E2E)
                                                ),
                                        shape = RoundedCornerShape(12.dp)
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        "Input relative pixel dimensions from your photo to calculate real-world size.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                MeasurementInput(
                                                        label =
                                                                "Reference Object (e.g. Credit Card) Pixels",
                                                        value = refPixels,
                                                        onValueChange = { refPixels = it }
                                                )

                                                MeasurementInput(
                                                        label =
                                                                "Reference Object Real Size (Inches)",
                                                        value = refInches,
                                                        onValueChange = { refInches = it }
                                                )

                                                HorizontalDivider(
                                                        modifier =
                                                                Modifier.padding(vertical = 12.dp),
                                                        color = Color.Gray.copy(alpha = 0.3f)
                                                )

                                                MeasurementInput(
                                                        label = "Target Object Pixels",
                                                        value = targetPixels,
                                                        onValueChange = { targetPixels = it }
                                                )

                                                Button(
                                                        onClick = {
                                                                val rp =
                                                                        refPixels.toFloatOrNull()
                                                                                ?: 1f
                                                                val ri =
                                                                        refInches.toFloatOrNull()
                                                                                ?: 1f
                                                                val tp =
                                                                        targetPixels.toFloatOrNull()
                                                                                ?: 0f
                                                                viewModel
                                                                        .calculateReferenceMeasurement(
                                                                                ri,
                                                                                rp,
                                                                                tp
                                                                        )
                                                        },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(top = 16.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                Color(0xFF4CAF50)
                                                                )
                                                ) {
                                                        Icon(
                                                                Icons.Default.Calculate,
                                                                contentDescription = null
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Calculate Result")
                                                }
                                        }
                                }

                                if (result != null) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        Color(0xFF4CAF50)
                                                                                .copy(alpha = 0.2f)
                                                        ),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                Column(
                                                        modifier =
                                                                Modifier.padding(24.dp)
                                                                        .fillMaxWidth(),
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        Text(
                                                                "Calculated Dimension",
                                                                color = Color.LightGray
                                                        )
                                                        Text(
                                                                "${String.format("%.2f", result)} inches",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineLarge,
                                                                color = Color(0xFF81C784),
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }
                        }
                } else {
                        // AR Mode
                        if (cameraPermissionState.status.isGranted) {
                                Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                                        ARMeasurementView(
                                                modifier = Modifier.fillMaxSize(),
                                                calculateDistance = { p1, p2 ->
                                                        viewModel.calculateArDistance(p1, p2)
                                                },
                                                onDistanceUpdated = { distance ->
                                                        viewModel.updateArDistance(distance)
                                                }
                                        )

                                        // Distance Overlay
                                        if (arDistance != null) {
                                                Card(
                                                        modifier =
                                                                Modifier.align(Alignment.TopCenter)
                                                                        .padding(16.dp),
                                                        colors =
                                                                CardDefaults.cardColors(
                                                                        containerColor =
                                                                                Color(0xFF1E1E2E)
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.9f
                                                                                        )
                                                                ),
                                                        shape = RoundedCornerShape(16.dp)
                                                ) {
                                                        Column(
                                                                modifier = Modifier.padding(16.dp),
                                                                horizontalAlignment =
                                                                        Alignment.CenterHorizontally
                                                        ) {
                                                                Text(
                                                                        "Distance",
                                                                        color = Color.LightGray,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall
                                                                )
                                                                Text(
                                                                        "${String.format("%.1f", arDistance)} inches",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .headlineMedium,
                                                                        color = Color(0xFF81C784),
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }
                                                }
                                        }
                                }
                        } else {
                                Box(
                                        modifier = Modifier.padding(padding).fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                "Camera permission required for AR Measurement.",
                                                color = Color.White
                                        )
                                }
                        }
                }
        }
}

@Composable
fun MeasurementInput(label: String, value: String, onValueChange: (String) -> Unit) {
        TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Number
                        ),
                colors =
                        TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                        )
        )
}
