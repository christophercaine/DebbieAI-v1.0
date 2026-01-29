package com.debbiedoesit.debbieai.photos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.photos.ui.components.PhotoCard
import com.debbiedoesit.debbieai.photos.ui.components.CategoryFilterChips
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: Long,
    viewModel: PhotoViewModel,
    onBackClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onAddPhotosClick: () -> Unit
) {
    val album by viewModel.currentAlbum.collectAsState()
    val photos by viewModel.currentAlbumPhotos.collectAsState()
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    // Load album on mount
    LaunchedEffect(albumId) {
        viewModel.loadAlbum(albumId)
    }
    
    // Filter photos
    val displayPhotos = remember(photos, filterCategory) {
        if (filterCategory == null) photos
        else photos.filter { it.category == filterCategory }
    }
    
    // Delete album confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Album?") },
            text = { 
                Text("This will delete the album \"${album?.name}\". Photos will not be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAlbum(albumId)
                        showDeleteDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit album dialog
    if (showEditDialog && album != null) {
        EditAlbumDialog(
            album = album!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedAlbum ->
                viewModel.updateAlbum(updatedAlbum)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                // Selection mode top bar
                TopAppBar(
                    title = { Text("${selectedPhotos.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll(displayPhotos.map { it.id }) }) {
                            Icon(Icons.Filled.SelectAll, contentDescription = "Select all")
                        }
                        IconButton(onClick = { 
                            // Remove from album (set albumId to null)
                            selectedPhotos.forEach { photoId ->
                                // TODO: Implement removeFromAlbum in ViewModel
                            }
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Remove from album")
                        }
                        if (selectedPhotos.size == 1) {
                            IconButton(onClick = { 
                                viewModel.setAlbumCover(albumId, selectedPhotos.first())
                                viewModel.clearSelection()
                            }) {
                                Icon(Icons.Filled.Image, contentDescription = "Set as cover")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                // Normal top bar
                TopAppBar(
                    title = { 
                        Column {
                            Text(album?.name ?: "Album")
                            if (album != null) {
                                Text(
                                    text = "${album!!.photoCount} photos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Favorite toggle
                        IconButton(onClick = { viewModel.toggleAlbumFavorite(albumId) }) {
                            Icon(
                                if (album?.isFavorite == true) Icons.Filled.Favorite
                                else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (album?.isFavorite == true) Color.Red
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // More options
                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More")
                            }
                            
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Album") },
                                    onClick = {
                                        showMoreMenu = false
                                        showEditDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Edit, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add Photos") },
                                    onClick = {
                                        showMoreMenu = false
                                        onAddPhotosClick()
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Add, null) }
                                )
                                
                                HorizontalDivider()
                                
                                DropdownMenuItem(
                                    text = { Text("Delete Album") },
                                    onClick = {
                                        showMoreMenu = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = { 
                                        Icon(
                                            Icons.Filled.Delete, 
                                            null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = onAddPhotosClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add photos")
                }
            }
        }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Album info card (if has description or location)
            if (album?.description?.isNotEmpty() == true || album?.hasLocation == true) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (album?.description?.isNotEmpty() == true) {
                            Text(
                                text = album!!.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (album?.hasLocation == true) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = if (album?.description?.isNotEmpty() == true) 8.dp else 0.dp)
                            ) {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = album!!.formattedAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // Before/After stats
            if ((album?.beforeCount ?: 0) > 0 || (album?.afterCount ?: 0) > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if ((album?.beforeCount ?: 0) > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFFF9800),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.size(12.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${album!!.beforeCount} Before",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    if ((album?.afterCount ?: 0) > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.size(12.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${album!!.afterCount} After",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            // Category filter
            CategoryFilterChips(
                selectedCategory = filterCategory,
                onCategorySelected = { viewModel.setFilterCategory(it) }
            )
            
            // Photo count
            Text(
                text = "${displayPhotos.size} photos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            
            if (displayPhotos.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (filterCategory != null)
                                "No ${filterCategory?.name?.lowercase()} photos in this album"
                            else
                                "No photos in this album",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = onAddPhotosClick) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photos")
                        }
                    }
                }
            } else {
                // Photo grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = displayPhotos,
                        key = { it.id }
                    ) { photo ->
                        PhotoCard(
                            photo = photo,
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(photo.id)
                                } else {
                                    onPhotoClick(photo)
                                }
                            },
                            onLongClick = { viewModel.toggleSelection(photo.id) },
                            isSelected = selectedPhotos.contains(photo.id),
                            isSelectionMode = isSelectionMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditAlbumDialog(
    album: com.debbiedoesit.debbieai.core.database.Album,
    onDismiss: () -> Unit,
    onSave: (com.debbiedoesit.debbieai.core.database.Album) -> Unit
) {
    var name by remember { mutableStateOf(album.name) }
    var description by remember { mutableStateOf(album.description) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Album") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Album Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onSave(album.copy(
                        name = name,
                        description = description,
                        updatedAt = java.time.LocalDateTime.now()
                    ))
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
