package com.debbiedoesit.debbieai.contacts.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.data.local.ContactType
import com.debbiedoesit.debbieai.contacts.ui.components.*
import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel
import com.debbiedoesit.debbieai.contacts.viewmodel.SyncState
import com.debbiedoesit.debbieai.core.ui.theme.DebbieRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    onAddClick: () -> Unit,
    onSyncContacts: () -> Unit,
    onContactClick: (Contact) -> Unit,
    onDuplicatesClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val contacts by viewModel.filteredContacts.collectAsState(initial = emptyList())
    val syncState by viewModel.syncState.collectAsState()
    val duplicateCount by viewModel.duplicateCount.collectAsState(initial = 0)
    val typeFilter by viewModel.typeFilter.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle sync results
    LaunchedEffect(syncState) {
        when (val state = syncState) {
            is SyncState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Synced: ${state.result.added} added, ${state.result.updated} updated"
                )
                viewModel.resetSyncState()
            }
            is SyncState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Sync failed: ${state.message}"
                )
                viewModel.resetSyncState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts (${contacts.size})") },
                actions = {
                    // Search
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    
                    // Duplicates with badge
                    if (duplicateCount > 0) {
                        IconButton(onClick = onDuplicatesClick) {
                            BadgedBox(
                                badge = { Badge { Text(duplicateCount.toString()) } }
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = "Duplicates")
                            }
                        }
                    }
                    
                    // Sync button
                    IconButton(
                        onClick = onSyncContacts,
                        enabled = syncState !is SyncState.Syncing
                    ) {
                        if (syncState is SyncState.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.Sync, contentDescription = "Sync Contacts")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = DebbieRed
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = "Add Contact")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Type filter chips
            ContactTypeFilterChips(
                selectedType = typeFilter,
                onTypeSelected = { viewModel.setTypeFilter(it) }
            )
            
            if (contacts.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (typeFilter != null) "No ${typeFilter?.name?.lowercase()} contacts"
                                   else "No contacts yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (typeFilter == null) {
                            Button(onClick = onSyncContacts) {
                                Icon(Icons.Filled.Sync, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync from Device")
                            }
                        } else {
                            TextButton(onClick = { viewModel.setTypeFilter(null) }) {
                                Text("Clear filter")
                            }
                        }
                    }
                }
            } else {
                // Contact list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = contacts,
                        key = { it.id }
                    ) { contact ->
                        ContactCard(
                            contact = contact,
                            onClick = { onContactClick(contact) }
                        )
                    }
                    
                    // Bottom spacer for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
