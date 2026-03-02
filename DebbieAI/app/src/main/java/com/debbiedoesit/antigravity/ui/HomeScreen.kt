package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debbiedoesit.antigravity.AntigravityApplication
import com.debbiedoesit.antigravity.DeviceCapabilityDetector
import com.debbiedoesit.antigravity.DeviceFeature
import com.debbiedoesit.antigravity.DeviceTier
import com.debbiedoesit.antigravity.R
import com.debbiedoesit.antigravity.utils.NetworkStatus

data class DashboardItem(
        val title: String,
        val subtitle: String,
        val icon: ImageVector,
        val logoRes: Int?,
        val feature: DeviceFeature?,
        val route: String,
        val accentColor: Color
)

@Composable
fun HomeScreen(tier: DeviceTier, onNavigate: (String) -> Unit) {
        val context = LocalContext.current
        val app = context.applicationContext as AntigravityApplication
        val networkStatus by
                app.networkMonitor.status.collectAsState(initial = NetworkStatus.Available)
        val isOffline = networkStatus == NetworkStatus.Unavailable

        val items =
                listOf(
                        DashboardItem(
                                "AI Chat",
                                "Ask Debbie anything",
                                Icons.Default.Chat,
                                R.drawable.debbieai_logo,
                                null,
                                "chat",
                                Color(0xFF6C63FF)
                        ),
                        DashboardItem(
                                "Job Photos",
                                "Capture & analyze",
                                Icons.Default.CameraAlt,
                                R.drawable.debphotoslogo,
                                DeviceFeature.YOLO_DETECTION,
                                "photos",
                                Color(0xFF00BCD4)
                        ),
                        DashboardItem(
                                "Contacts",
                                "Customer CRM",
                                Icons.Default.People,
                                R.drawable.debcontactslogo,
                                null,
                                "contacts",
                                Color(0xFF2196F3)
                        ),
                        DashboardItem(
                                "Measurements",
                                "AR + depth sensing",
                                Icons.Default.Straighten,
                                null,
                                DeviceFeature.DEPTH_MEASUREMENT,
                                "measure",
                                Color(0xFF4CAF50)
                        ),
                        DashboardItem(
                                "Video Tools",
                                "Timelapse & reels",
                                Icons.Default.Videocam,
                                null,
                                DeviceFeature.FFMPEG_VIDEO,
                                "video",
                                Color(0xFFFF5722)
                        ),
                        DashboardItem(
                                "Marketing",
                                "Social content AI",
                                Icons.Default.AutoAwesome,
                                null,
                                DeviceFeature.STYLE_TRANSFER,
                                "marketing",
                                Color(0xFFE91E63)
                        ),
                        DashboardItem(
                                "Floor Plans",
                                "AI drafting",
                                Icons.Default.Architecture,
                                R.drawable.debdraftinglogo,
                                DeviceFeature.FLOOR_PLAN_AI,
                                "floor_plan",
                                Color(0xFFFF9800)
                        ),
                        DashboardItem(
                                "Details",
                                "Tasks & calendar",
                                Icons.Default.Checklist,
                                R.drawable.debdetailslogo,
                                null,
                                "details",
                                Color(0xFF9C27B0)
                        ),
                        DashboardItem(
                                "Data",
                                "Business insights",
                                Icons.Default.Insights,
                                R.drawable.debdatalogo,
                                null,
                                "data",
                                Color(0xFF009688)
                        )
                )

        Box(modifier = Modifier.fillMaxSize()) {
                // Background gradient
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF121212),
                                                                        Color(0xFF1E1E2E)
                                                                )
                                                )
                                        )
                )

                Column(modifier = Modifier.fillMaxSize()) {
                        // Header with logo + Debbie avatar
                        DebbieHeader(isOffline, tier)

                        // Quick AI Chat CTA
                        AIChatBanner { onNavigate("chat") }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                                text = "Contractor Suite",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFB0B0C0),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.fillMaxSize()
                        ) {
                                items(items) { item ->
                                        val isEnabled =
                                                item.feature == null ||
                                                        DeviceCapabilityDetector.isFeatureAvailable(
                                                                item.feature,
                                                                tier
                                                        )
                                        FeatureCard(item, isEnabled) {
                                                if (isEnabled) onNavigate(item.route)
                                        }
                                }
                        }
                }
        }
}

@Composable
fun DebbieHeader(isOffline: Boolean, tier: DeviceTier) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        Brush.horizontalGradient(
                                                colors =
                                                        listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                                        )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        // Main logo
                        Image(
                                painter = painterResource(id = R.drawable.logo_main),
                                contentDescription = "Antigravity Logo",
                                modifier = Modifier.height(44.dp).wrapContentWidth(),
                                contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Offline badge
                        if (isOffline) {
                                Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFF5252).copy(alpha = 0.2f),
                                        modifier = Modifier.padding(end = 8.dp)
                                ) {
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 8.dp,
                                                                vertical = 4.dp
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Default.WifiOff,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFF5252),
                                                        modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        "OFFLINE",
                                                        color = Color(0xFFFF5252),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }
                        }

                        // Debbie avatar
                        Image(
                                painter = painterResource(id = R.drawable.debbie_illustration),
                                contentDescription = "Debbie AI",
                                modifier =
                                        Modifier.size(44.dp)
                                                .clip(CircleShape)
                                                .shadow(4.dp, CircleShape),
                                contentScale = ContentScale.Crop
                        )
                }

                // Tier chip at bottom
                Row(modifier = Modifier.align(Alignment.BottomEnd).padding(top = 48.dp)) {
                        Surface(
                                shape = RoundedCornerShape(4.dp),
                                color =
                                        when (tier) {
                                                DeviceTier.PRO ->
                                                        Color(0xFF6C63FF).copy(alpha = 0.25f)
                                                DeviceTier.STANDARD ->
                                                        Color(0xFF4CAF50).copy(alpha = 0.25f)
                                                DeviceTier.LITE ->
                                                        Color(0xFF9E9E9E).copy(alpha = 0.25f)
                                        }
                        ) {
                                Text(
                                        text = "⚡ ${tier.name} TIER",
                                        color =
                                                when (tier) {
                                                        DeviceTier.PRO -> Color(0xFF9C94FF)
                                                        DeviceTier.STANDARD -> Color(0xFF81C784)
                                                        DeviceTier.LITE -> Color(0xFFBDBDBD)
                                                },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier =
                                                Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                        }
                }
        }
}

@Composable
fun AIChatBanner(onClick: () -> Unit) {
        Card(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onClick() }
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                Brush.horizontalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF6C63FF),
                                                                        Color(0xFF9C94FF)
                                                                )
                                                )
                                        )
                                        .padding(16.dp)
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                        painter =
                                                painterResource(
                                                        id = R.drawable.debbie_illustration
                                                ),
                                        contentDescription = "Debbie",
                                        modifier = Modifier.size(52.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Chat with Debbie AI",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                        )
                                        Text(
                                                "Estimates · Materials · Codes · Advice",
                                                color = Color.White.copy(alpha = 0.8f),
                                                style = MaterialTheme.typography.labelMedium
                                        )
                                }
                                Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }
        }
}

@Composable
fun FeatureCard(item: DashboardItem, isEnabled: Boolean, onClick: () -> Unit) {
        val alpha = if (isEnabled) 1f else 0.4f

        if (item.logoRes != null && isEnabled) {
                // Render just the logo as the button
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable(enabled = isEnabled) { onClick() },
                        contentAlignment = Alignment.Center
                ) {
                        Image(
                                painter = painterResource(id = item.logoRes),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                        )
                }
        } else {
                // Render standard card for items without logos
                Card(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clickable(enabled = isEnabled) { onClick() }
                                        .shadow(
                                                if (isEnabled) 6.dp else 0.dp,
                                                RoundedCornerShape(16.dp)
                                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
                ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                                // Accent color top bar
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(4.dp)
                                                        .background(
                                                                item.accentColor.copy(alpha = alpha)
                                                        )
                                )

                                Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(52.dp)
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(
                                                                        item.accentColor.copy(
                                                                                alpha =
                                                                                        if (isEnabled
                                                                                        )
                                                                                                0.15f
                                                                                        else 0.06f
                                                                        )
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.title,
                                                        modifier = Modifier.size(28.dp),
                                                        tint = item.accentColor.copy(alpha = alpha)
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = alpha),
                                                textAlign = TextAlign.Center
                                        )

                                        Text(
                                                text =
                                                        if (isEnabled) item.subtitle
                                                        else "Upgrade Required",
                                                style = MaterialTheme.typography.labelSmall,
                                                color =
                                                        if (isEnabled) Color(0xFF8888A0)
                                                        else Color(0xFFFF5252).copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
                                        )
                                }
                        }
                }
        }
}

@Composable
fun FeaturePlaceholder(name: String) {
        Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                                Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF6C63FF)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                name,
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                "Coming Soon",
                                color = Color(0xFF8888A0),
                                style = MaterialTheme.typography.bodyMedium
                        )
                }
        }
}
