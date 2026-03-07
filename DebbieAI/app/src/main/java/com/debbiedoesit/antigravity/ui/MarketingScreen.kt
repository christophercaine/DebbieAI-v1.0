package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debbiedoesit.antigravity.R
import com.debbiedoesit.antigravity.viewmodel.JobSitePhotoViewModel
import com.debbiedoesit.antigravity.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingScreen(
        viewModel: MarketingViewModel = viewModel(),
        photoViewModel: JobSitePhotoViewModel = viewModel(),
        onBack: () -> Unit = {}
) {
        val photos by photoViewModel.photos.collectAsState()
        val photoPaths = photos.map { it.filePath }
        var projectName by remember { mutableStateOf("") }
        var features by remember { mutableStateOf("") }
        val generatedCaption by viewModel.generatedCaption.collectAsState()
        val isGenerating by viewModel.isGenerating.collectAsState()
        val clipboardManager = LocalClipboardManager.current

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("AI Marketing Content", color = Color.White) },
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
                Column(
                        modifier =
                                Modifier.padding(padding)
                                        .padding(16.dp)
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                ) {
                        Image(
                                painter = painterResource(id = R.drawable.ic_marketing_new),
                                contentDescription = "Debbie Does Marketing",
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                "Generate Social Media Posts",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFE91E63),
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                "Let Debbie write your project highlights for Instagram & Facebook.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                                value = projectName,
                                onValueChange = { projectName = it },
                                label = { Text("Project Name (e.g. Smith Kitchen Remodel)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color.Gray,
                                                focusedBorderColor = Color(0xFFE91E63),
                                                focusedLabelColor = Color(0xFFE91E63),
                                                unfocusedLabelColor = Color.Gray,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                        )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                                value = features,
                                onValueChange = { features = it },
                                label = { Text("Key Features (comma separated)") },
                                placeholder = {
                                        Text("quartz counters, custom lighting, sub-zero fridge")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color.Gray,
                                                focusedBorderColor = Color(0xFFE91E63),
                                                focusedLabelColor = Color(0xFFE91E63),
                                                unfocusedLabelColor = Color.Gray,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                        )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                                onClick = {
                                        viewModel.generateCaption(
                                                projectName,
                                                features.split(",").map { it.trim() }
                                        )
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = !isGenerating && projectName.isNotBlank(),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFE91E63)
                                        )
                        ) {
                                if (isGenerating) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White
                                        )
                                } else {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Generate Post Copy")
                                }
                        }

                        if (generatedCaption.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(32.dp))

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
                                                        "Generated Result",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = Color(0xFFE91E63),
                                                        fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                        generatedCaption,
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodyMedium
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End
                                                ) {
                                                        IconButton(
                                                                onClick = {
                                                                        clipboardManager.setText(
                                                                                AnnotatedString(
                                                                                        generatedCaption
                                                                                )
                                                                        )
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.ContentCopy,
                                                                        contentDescription = "Copy",
                                                                        tint = Color.Gray
                                                                )
                                                        }
                                                        IconButton(onClick = { /* Share Logic */}) {
                                                                Icon(
                                                                        Icons.Default.Share,
                                                                        contentDescription =
                                                                                "Share",
                                                                        tint = Color.Gray
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                "Logo Magic",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFE91E63),
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                "Transform your logo with AI styles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val context = androidx.compose.ui.platform.LocalContext.current
                        val generatedLogo by viewModel.generatedLogo.collectAsState()

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                com.debbiedoesit.antigravity.ai.LogoVariation.values().forEach {
                                        variation ->
                                        AssistChip(
                                                onClick = {
                                                        val bitmap =
                                                                android.graphics.BitmapFactory
                                                                        .decodeResource(
                                                                                context.resources,
                                                                                R.drawable
                                                                                        .debbieai_logo
                                                                        )
                                                        viewModel.generateLogoVariation(
                                                                bitmap,
                                                                variation
                                                        )
                                                },
                                                label = {
                                                        Text(
                                                                variation.name,
                                                                color = Color.LightGray
                                                        )
                                                },
                                                colors =
                                                        AssistChipDefaults.assistChipColors(
                                                                containerColor = Color(0xFF1E1E2E)
                                                        )
                                        )
                                }
                        }

                        if (generatedLogo != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color(0xFF1E1E2E)
                                                )
                                ) {
                                        Image(
                                                bitmap = generatedLogo!!.asImageBitmap(),
                                                contentDescription = "Generated Logo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale =
                                                        androidx.compose.ui.layout.ContentScale.Fit
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                                "Marketing Reels",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFE91E63),
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                "Turn job site photos into viral videos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                        )

                        val exportStatus by viewModel.videoExportStatus.collectAsState()

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Button(
                                        onClick = {
                                                viewModel.createReel(
                                                        photoPaths,
                                                        com.debbiedoesit.antigravity.ai.ReelStyle
                                                                .BEFORE_AFTER,
                                                        projectName
                                                )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF1A1A2E)
                                                )
                                ) { Text("Before/After", fontSize = 12.sp) }
                                Button(
                                        onClick = {
                                                viewModel.createReel(
                                                        photoPaths,
                                                        com.debbiedoesit.antigravity.ai.ReelStyle
                                                                .TIMELAPSE,
                                                        projectName
                                                )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF1A1A2E)
                                                )
                                ) { Text("Timelapse", fontSize = 12.sp) }
                        }

                        if (exportStatus.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        exportStatus,
                                        color = Color.Cyan,
                                        style = MaterialTheme.typography.labelSmall
                                )
                        }
                }
        }
}
