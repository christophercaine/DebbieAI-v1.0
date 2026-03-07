package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.isTracking
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.rememberEngine

@Composable
fun ARMeasurementView(
        modifier: Modifier = Modifier,
        calculateDistance: (Pose, Pose) -> Float,
        onDistanceUpdated: (Float) -> Unit
) {
        val engine = rememberEngine()
        var startNode by remember { mutableStateOf<AnchorNode?>(null) }
        var endNode by remember { mutableStateOf<AnchorNode?>(null) }
        var currentHitResult by remember { mutableStateOf<HitResult?>(null) }

        Box(modifier = modifier) {
                ARScene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        planeRenderer = true,
                        onSessionUpdated = { _, frame ->
                                if (frame.camera.isTracking) {
                                        // Perform a hit test from the center of the screen
                                        val hitResults =
                                                frame.hitTest(
                                                        frame.camera
                                                                .imageIntrinsics
                                                                .imageDimensions[0] / 2f,
                                                        frame.camera
                                                                .imageIntrinsics
                                                                .imageDimensions[1] / 2f
                                                )
                                        currentHitResult =
                                                hitResults.firstOrNull { it.trackable.isTracking }

                                        if (startNode != null &&
                                                        endNode == null &&
                                                        currentHitResult != null
                                        ) {
                                                val distance =
                                                        calculateDistance(
                                                                startNode!!.anchor?.pose
                                                                        ?: Pose.IDENTITY,
                                                                currentHitResult!!.hitPose
                                                        )
                                                onDistanceUpdated(distance)
                                        }
                                }
                        }
                ) {
                        startNode?.let { childNodes += it }
                        endNode?.let { childNodes += it }

                        // Draw a temporary indicator at the center point if we are tracking
                        currentHitResult?.let { hit ->
                                if (endNode == null) {
                                        // Update current indicator pose (could be a sphere or
                                        // crosshair model)
                                }
                        }
                }

                // Center Reticle
                Box(
                        modifier =
                                Modifier.align(Alignment.Center)
                                        .size(8.dp)
                                        .background(Color.White, CircleShape)
                )

                // Controls
                Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        FloatingActionButton(
                                onClick = {
                                        startNode = null
                                        endNode = null
                                        onDistanceUpdated(0f)
                                },
                                containerColor = Color.Red.copy(alpha = 0.8f)
                        ) {
                                Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Reset",
                                        tint = Color.White
                                )
                        }

                        FloatingActionButton(
                                onClick = {
                                        currentHitResult?.let { hit ->
                                                if (startNode == null) {
                                                        startNode =
                                                                AnchorNode(
                                                                        engine,
                                                                        hit.trackable.createAnchor(
                                                                                hit.hitPose
                                                                        )
                                                                )
                                                } else if (endNode == null) {
                                                        endNode =
                                                                AnchorNode(
                                                                        engine,
                                                                        hit.trackable.createAnchor(
                                                                                hit.hitPose
                                                                        )
                                                                )
                                                        val distance =
                                                                calculateDistance(
                                                                        startNode!!.anchor?.pose
                                                                                ?: Pose.IDENTITY,
                                                                        endNode!!.anchor?.pose
                                                                                ?: Pose.IDENTITY
                                                                )
                                                        onDistanceUpdated(distance)
                                                }
                                        }
                                },
                                containerColor = Color(0xFF4CAF50)
                        ) {
                                Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add Point",
                                        tint = Color.White
                                )
                        }
                }
        }
}
