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
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.photos.ui.components.PhotoCard
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: PhotoViewModel,
    onBackClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    val favorites by viewModel.favoritePhotos.collectAsState(initial = emptyList())
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedPhotos.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll(favorites.map { it.id }) }) {
                            Icon(Icons.Filled.SelectAll, contentDescription = "Select all")
                        }
                        // Unfavorite selected
                        IconButton(onClick = { 
                            selectedPhotos.forEach { viewModel.toggleFavorite(it) }
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Filled.HeartBroken, contentDescription = "Remove from favorites")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                TopAppBar(
                    title = { Text("Favorites") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap the heart icon on any photo to add it here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "${favorites.size} favorites",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = favorites,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: PhotoViewModel,
    onBackClick: () -> Unit,
    onPhotoClick: (Photo) -> Unit
) {
    val trashedPhotos by viewModel.trashedPhotos.collectAsState(initial = emptyList())
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    
    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    
    // Empty trash confirmation
    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            icon = { Icon(Icons.Filled.DeleteForever, null) },
            title = { Text("Empty Trash?") },
            text = { 
                Text("This will permanently delete ${trashedPhotos.size} photos. This cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        trashedPhotos.forEach { viewModel.permanentlyDelete(it.id) }
                        showEmptyTrashDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Empty Trash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyTrashDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedPhotos.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll(trashedPhotos.map { it.id }) }) {
                            Icon(Icons.Filled.SelectAll, contentDescription = "Select all")
                        }
                        // Restore selected
                        IconButton(onClick = { 
                            selectedPhotos.forEach { viewModel.restoreFromTrash(it) }
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Filled.Restore, contentDescription = "Restore")
                        }
                        // Permanently delete selected
                        IconButton(onClick = { 
                            selectedPhotos.forEach { viewModel.permanentlyDelete(it) }
                            viewModel.clearSelection()
                        }) {
                            Icon(
                                Icons.Filled.DeleteForever, 
                                contentDescription = "Delete permanently",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                TopAppBar(
                    title = { Text("Trash") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (trashedPhotos.isNotEmpty()) {
                            TextButton(onClick = { showEmptyTrashDialog = true }) {
                                Text(
                                    "Empty Trash",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        
        if (trashedPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Trash is empty",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Deleted photos will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Info banner
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${trashedPhotos.size} photos in trash. Items in trash will be permanently deleted after 30 days.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = trashedPhotos,
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
