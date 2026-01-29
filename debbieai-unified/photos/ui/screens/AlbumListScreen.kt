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
import com.debbiedoesit.debbieai.core.database.Album
import com.debbiedoesit.debbieai.core.database.AlbumType
import com.debbiedoesit.debbieai.photos.ui.components.AlbumCard
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    viewModel: PhotoViewModel,
    onBackClick: () -> Unit,
    onAlbumClick: (Album) -> Unit
) {
    val albums by viewModel.allAlbums.collectAsState(initial = emptyList())
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<AlbumType?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    // Filter albums
    val displayAlbums = remember(albums, filterType) {
        if (filterType == null) albums
        else albums.filter { it.type == filterType }
    }
    
    // Create album dialog
    if (showCreateDialog) {
        CreateAlbumDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, type, description ->
                viewModel.createAlbum(name, type, description)
                showCreateDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Albums") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Filter button
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Badge(
                                modifier = Modifier.offset(x = 8.dp, y = (-8).dp),
                                containerColor = if (filterType != null) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surface
                            ) {
                                if (filterType != null) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                        }
                        
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Types") },
                                onClick = {
                                    filterType = null
                                    showFilterMenu = false
                                },
                                trailingIcon = if (filterType == null) {
                                    { Icon(Icons.Filled.Check, null) }
                                } else null
                            )
                            
                            HorizontalDivider()
                            
                            AlbumType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.displayName) },
                                    onClick = {
                                        filterType = type
                                        showFilterMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(getAlbumTypeIcon(type), null)
                                    },
                                    trailingIcon = if (filterType == type) {
                                        { Icon(Icons.Filled.Check, null) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create album")
            }
        }
    ) { paddingValues ->
        
        if (displayAlbums.isEmpty()) {
            // Empty state
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
                        Icons.Filled.PhotoAlbum,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (filterType != null) 
                            "No ${filterType?.displayName?.lowercase()} albums" 
                        else 
                            "No albums yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Create albums to organize your photos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Album")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Stats bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${displayAlbums.size} albums",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "${displayAlbums.sumOf { it.photoCount }} total photos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Album grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = displayAlbums,
                        key = { it.id }
                    ) { album ->
                        AlbumCard(
                            album = album,
                            onClick = { onAlbumClick(album) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, type: AlbumType, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AlbumType.CUSTOM) }
    var showTypeMenu by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Album") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Album Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Type selector
                Box {
                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = { },
                        label = { Text("Type") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTypeMenu = true }) {
                                Icon(Icons.Filled.ArrowDropDown, null)
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false }
                    ) {
                        AlbumType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    showTypeMenu = false
                                },
                                leadingIcon = {
                                    Icon(getAlbumTypeIcon(type), null)
                                }
                            )
                        }
                    }
                }
                
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, selectedType, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper properties and functions
private val AlbumType.displayName: String
    get() = when (this) {
        AlbumType.JOB -> "Job"
        AlbumType.PROJECT -> "Project"
        AlbumType.CUSTOMER -> "Customer"
        AlbumType.DATE -> "Date"
        AlbumType.LOCATION -> "Location"
        AlbumType.CUSTOM -> "Custom"
        AlbumType.SMART -> "Smart Album"
    }

private fun getAlbumTypeIcon(type: AlbumType) = when (type) {
    AlbumType.JOB -> Icons.Filled.Work
    AlbumType.PROJECT -> Icons.Filled.Folder
    AlbumType.CUSTOMER -> Icons.Filled.Person
    AlbumType.DATE -> Icons.Filled.DateRange
    AlbumType.LOCATION -> Icons.Filled.LocationOn
    AlbumType.CUSTOM -> Icons.Filled.PhotoLibrary
    AlbumType.SMART -> Icons.Filled.AutoAwesome
}
