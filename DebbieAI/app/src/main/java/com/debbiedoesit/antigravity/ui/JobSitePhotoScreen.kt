package com.debbiedoesit.antigravity.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.debbiedoesit.antigravity.data.JobSitePhoto
import com.debbiedoesit.antigravity.viewmodel.JobSitePhotoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSitePhotoScreen(viewModel: JobSitePhotoViewModel = viewModel(), onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    var photoFile by remember { mutableStateOf<File?>(null) }
    val takePhotoLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && photoFile != null) {
                    viewModel.processCapturedPhotoFile(photoFile!!)
                }
            }

    fun launchCamera() {
        val file = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
        photoFile = file
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        takePhotoLauncher.launch(uri)
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Job Site Photos", color = Color.White) },
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
                            IconButton(onClick = { viewModel.syncWithGooglePhotos() }) {
                                Icon(
                                        Icons.Default.CloudSync,
                                        contentDescription = "Sync",
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
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { launchCamera() },
                        containerColor = Color(0xFF00BCD4),
                        contentColor = Color.White
                ) { Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo") }
            },
            containerColor = Color(0xFF121212)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isProcessing) {
                LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00BCD4)
                )
                Text(
                        "AI Analyzing photo tags & text...",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF00BCD4),
                        style = MaterialTheme.typography.bodySmall
                )
            }

            if (photos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No photos captured yet", color = Color.Gray)
                }
            } else {
                LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) { items(photos) { photo -> PhotoCard(photo) } }
            }
        }
    }
}

@Composable
fun PhotoCard(photo: JobSitePhoto) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                    model = File(photo.filePath),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4),
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = photo.photoType.name,
                            color = Color(0xFF00BCD4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                    )
                }

                if (photo.detectedObjects.isNotEmpty()) {
                    FlowRow(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        photo.detectedObjects.forEach { obj ->
                            Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(alpha = 0.1f)
                            ) {
                                Text(
                                        text = obj,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 6.dp,
                                                        vertical = 2.dp
                                                ),
                                        color = Color.White,
                                        fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                if (photo.extractedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                text = photo.extractedText.firstOrNull() ?: "",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
        modifier: Modifier = Modifier,
        horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            content = { content() }
    )
}
