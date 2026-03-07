package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debbiedoesit.antigravity.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoToolsScreen(onBack: () -> Unit = {}) {
        val tools =
                listOf(
                        VideoTool("Timelapse", Icons.Default.Timer, Color(0xFFFF9800)),
                        VideoTool("Reels AI", Icons.Default.AutoAwesome, Color(0xFFE91E63)),
                        VideoTool("Video Editor", Icons.Default.Edit, Color(0xFF2196F3)),
                        VideoTool("Capture", Icons.Default.Videocam, Color(0xFFFF5722))
                )

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Video Tools", color = Color.White) },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = Color.White
                                                )
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
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                        Image(
                                painter = painterResource(id = R.drawable.ic_video_new),
                                contentDescription = "Debbie Does Videos",
                                modifier = Modifier.fillMaxWidth().height(150.dp).padding(16.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )

                        LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) { items(tools) { tool -> VideoToolCard(tool) } }
                }
        }
}

@Composable
fun VideoToolCard(tool: VideoTool) {
        Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                shape = RoundedCornerShape(16.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Icon(
                                tool.icon,
                                contentDescription = tool.name,
                                modifier = Modifier.size(48.dp),
                                tint = tool.color
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                tool.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                        )
                }
        }
}

data class VideoTool(val name: String, val icon: ImageVector, val color: Color)
