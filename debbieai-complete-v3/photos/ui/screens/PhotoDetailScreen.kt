package com.debbiedoesit.debbieai.photos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.core.database.PhotoCategory
import com.debbiedoesit.debbieai.core.ui.theme.PhotoCategoryColors
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel
import java.io.File
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: Long,
    viewModel: PhotoViewModel,
    onBackClick: () -> Unit,
    onLinkContactClick: (Long) -> Unit = {},
    onViewAlbumClick: (Long) -> Unit = {}
) {
    val photo by viewModel.currentPhoto.collectAsState()
    val context = LocalContext.current
    
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    
    // Load photo on mount
    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Move to Trash?") },
            text = { Text("This photo will be moved to trash. You can restore it later.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.moveToTrash(photoId)
                        showDeleteDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Move to Trash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Add tag dialog
    if (showTagDialog) {
        var newTag by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Add Tag") },
            text = {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text("Tag name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTag.isNotBlank()) {
                            viewModel.addTagToPhoto(photoId, newTag.trim())
                        }
                        showTagDialog = false
                    },
                    enabled = newTag.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        photo?.displayName ?: "Photo",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Favorite toggle
                    IconButton(onClick = { viewModel.toggleFavorite(photoId) }) {
                        Icon(
                            if (photo?.isFavorite == true) Icons.Filled.Favorite 
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (photo?.isFavorite == true) Color.Red 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Share
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                    }
                    
                    // Delete
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        
        if (photo == null) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Photo image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(File(photo!!.filePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = photo!!.description.ifEmpty { photo!!.displayName },
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Quick actions bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Category selector
                    Box {
                        AssistChip(
                            onClick = { showCategoryMenu = true },
                            label = { Text(photo!!.category.name) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PhotoCategoryColors.getColor(photo!!.category))
                                )
                            }
                        )
                        
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            PhotoCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.updateCategory(photoId, category)
                                        showCategoryMenu = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PhotoCategoryColors.getColor(category))
                                        )
                                    },
                                    trailingIcon = if (photo!!.category == category) {
                                        { Icon(Icons.Filled.Check, null) }
                                    } else null
                                )
                            }
                        }
                    }
                    
                    // Link to contact
                    AssistChip(
                        onClick = { onLinkContactClick(photoId) },
                        label = { 
                            Text(if (photo!!.contactId != null) "Linked" else "Link Contact")
                        },
                        leadingIcon = {
                            Icon(
                                if (photo!!.contactId != null) Icons.Filled.Person
                                else Icons.Filled.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    
                    // Add tag
                    AssistChip(
                        onClick = { showTagDialog = true },
                        label = { Text("Add Tag") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Label,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                
                HorizontalDivider()
                
                // Tags section
                if (photo!!.allTags.isNotEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            photo!!.allTags.forEach { tag ->
                                InputChip(
                                    selected = false,
                                    onClick = { /* TODO: Filter by tag */ },
                                    label = { Text(tag) },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { viewModel.removeTagFromPhoto(photoId, tag) },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Remove tag",
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    HorizontalDivider()
                }
                
                // Description section (editable)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var editingDescription by remember { mutableStateOf(false) }
                    var descriptionText by remember(photo) { 
                        mutableStateOf(photo!!.description) 
                    }
                    
                    if (editingDescription) {
                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Add a description...") },
                            maxLines = 3
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { 
                                descriptionText = photo!!.description
                                editingDescription = false 
                            }) {
                                Text("Cancel")
                            }
                            TextButton(onClick = {
                                viewModel.updateDescription(photoId, descriptionText)
                                editingDescription = false
                            }) {
                                Text("Save")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = photo!!.description.ifEmpty { "No description" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (photo!!.description.isEmpty()) 
                                    MaterialTheme.colorScheme.onSurfaceVariant 
                                else 
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { editingDescription = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
                
                HorizontalDivider()
                
                // Metadata section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Date taken
                    photo!!.dateTaken?.let { date ->
                        MetadataRow(
                            icon = Icons.Filled.CalendarToday,
                            label = "Date Taken",
                            value = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a"))
                        )
                    }
                    
                    // Location
                    if (photo!!.hasLocation) {
                        MetadataRow(
                            icon = Icons.Filled.LocationOn,
                            label = "Location",
                            value = photo!!.formattedLocation
                        )
                    }
                    
                    // Dimensions
                    if (photo!!.width > 0 && photo!!.height > 0) {
                        MetadataRow(
                            icon = Icons.Filled.AspectRatio,
                            label = "Dimensions",
                            value = "${photo!!.width} × ${photo!!.height}"
                        )
                    }
                    
                    // File size
                    MetadataRow(
                        icon = Icons.Filled.Storage,
                        label = "Size",
                        value = formatFileSize(photo!!.fileSize)
                    )
                    
                    // Device
                    if (photo!!.deviceMake.isNotEmpty() || photo!!.deviceModel.isNotEmpty()) {
                        MetadataRow(
                            icon = Icons.Filled.CameraAlt,
                            label = "Camera",
                            value = listOf(photo!!.deviceMake, photo!!.deviceModel)
                                .filter { it.isNotEmpty() }
                                .joinToString(" ")
                        )
                    }
                    
                    // File name
                    MetadataRow(
                        icon = Icons.Filled.InsertDriveFile,
                        label = "File Name",
                        value = photo!!.fileName
                    )
                }
                
                // AI Analysis section (if available)
                if (photo!!.aiDescription.isNotEmpty() || 
                    photo!!.aiObjects.isNotEmpty() || 
                    photo!!.aiText.isNotEmpty()) {
                    
                    HorizontalDivider()
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Analysis",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (photo!!.aiDescription.isNotEmpty()) {
                            Text(
                                text = photo!!.aiDescription,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        if (photo!!.aiObjects.isNotEmpty()) {
                            Text(
                                text = "Detected Objects",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                photo!!.aiObjects.forEach { obj ->
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text(obj) }
                                    )
                                }
                            }
                        }
                        
                        if (photo!!.aiText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Detected Text",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = photo!!.aiText,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun MetadataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
