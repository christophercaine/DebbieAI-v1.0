package com.debbiedoesit.debbieai.photos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.core.database.PhotoCategory
import com.debbiedoesit.debbieai.photos.ui.components.*
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel
import com.debbiedoesit.debbieai.photos.viewmodel.ImportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    viewModel: PhotoViewModel,
    onPhotoClick: (Photo) -> Unit,
    onAlbumsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onImportClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onTrashClick: () -> Unit
) {
    val photos by viewModel.allPhotos.collectAsState(initial = emptyList())
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val importState by viewModel.importState.collectAsState()
    
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_DESC) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle import state changes
    LaunchedEffect(importState) {
        when (val state = importState) {
            is ImportState.Success -> {
                snackbarHostState.showSnackbar(
                    "Imported ${state.imported} photos" + 
                    if (state.skipped > 0) " (${state.skipped} skipped)" else ""
                )
                viewModel.resetImportState()
            }
            is ImportState.Error -> {
                snackbarHostState.showSnackbar("Import failed: ${state.message}")
                viewModel.resetImportState()
            }
            else -> {}
        }
    }
    
    // Filter and sort photos
    val displayPhotos = remember(photos, filterCategory, sortOrder) {
        photos
            .filter { photo ->
                filterCategory == null || photo.category == filterCategory
            }
            .sortedWith(
                when (sortOrder) {
                    SortOrder.DATE_DESC -> compareByDescending { it.dateTaken ?: it.createdAt }
                    SortOrder.DATE_ASC -> compareBy { it.dateTaken ?: it.createdAt }
                    SortOrder.NAME_ASC -> compareBy { it.fileName.lowercase() }
                    SortOrder.NAME_DESC -> compareByDescending { it.fileName.lowercase() }
                    SortOrder.SIZE_DESC -> compareByDescending { it.fileSize }
                    SortOrder.SIZE_ASC -> compareBy { it.fileSize }
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
                            Icon(Icons.Filled.Close, contentDescription = "Cancel selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll(displayPhotos.map { it.id }) }) {
                            Icon(Icons.Filled.SelectAll, contentDescription = "Select all")
                        }
                        IconButton(onClick = { viewModel.favoriteSelected() }) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
                        }
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            } else {
                // Normal top bar
                TopAppBar(
                    title = { Text("Photos") },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onAlbumsClick) {
                            Icon(Icons.Filled.PhotoAlbum, contentDescription = "Albums")
                        }
                        
                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More")
                            }
                            
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Favorites") },
                                    onClick = { 
                                        showMoreMenu = false
                                        onFavoritesClick()
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Favorite, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Trash") },
                                    onClick = {
                                        showMoreMenu = false
                                        onTrashClick()
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Delete, null) }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Import Photos") },
                                    onClick = {
                                        showMoreMenu = false
                                        onImportClick()
                                    },
                                    leadingIcon = { Icon(Icons.Filled.Add, null) }
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
                    onClick = onImportClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = "Import photos")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filter chips
            CategoryFilterChips(
                selectedCategory = filterCategory,
                onCategorySelected = { viewModel.setFilterCategory(it) }
            )
            
            // View controls row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${displayPhotos.size} photos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sort button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Filled.Sort, contentDescription = "Sort")
                        }
                        SortOrderMenu(
                            currentSort = sortOrder,
                            onSortChanged = { sortOrder = it },
                            expanded = showSortMenu,
                            onDismiss = { showSortMenu = false }
                        )
                    }
                    
                    // View mode toggle
                    ViewModeToggle(
                        currentMode = viewMode,
                        onModeChanged = { viewMode = it }
                    )
                }
            }
            
            // Import progress indicator
            AnimatedVisibility(visible = importState is ImportState.Importing) {
                val state = importState as? ImportState.Importing
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { (state?.current?.toFloat() ?: 0f) / (state?.total?.toFloat() ?: 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Importing ${state?.current ?: 0} of ${state?.total ?: 0}...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Photo grid or list
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
                                "No ${filterCategory?.name?.lowercase()} photos" 
                            else 
                                "No photos yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Import photos from your device to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onImportClick) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import Photos")
                        }
                    }
                }
            } else {
                when (viewMode) {
                    ViewMode.GRID -> {
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
                    ViewMode.LIST -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = displayPhotos,
                                key = { it.id }
                            ) { photo ->
                                PhotoListItem(
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
                    ViewMode.TIMELINE -> {
                        // Group by date
                        val groupedPhotos = displayPhotos.groupBy { photo ->
                            photo.dateTaken?.toLocalDate() ?: photo.createdAt.toLocalDate()
                        }.toSortedMap(compareByDescending { it })
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            groupedPhotos.forEach { (date, datePhotos) ->
                                item {
                                    Text(
                                        text = date.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                
                                items(
                                    items = datePhotos,
                                    key = { it.id }
                                ) { photo ->
                                    PhotoListItem(
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
                                        isSelectionMode = isSelectionMode,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
