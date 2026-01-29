package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.viewmodel.ContactViewModel
import com.debbiedoesdetails.app.viewmodel.SyncState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    onAddClick: () -> Unit,
    onSyncContacts: () -> Unit,
    onContactClick: (Contact) -> Unit,
    onDuplicatesClick: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState(initial = emptyList())
    val syncState by viewModel.syncState.collectAsState()
    val duplicateCount = contacts.count { it.isDuplicate }

    // Show snackbar for sync results
    val snackbarHostState = remember { SnackbarHostState() }
    
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
                    // Duplicates button with badge
                    IconButton(onClick = onDuplicatesClick) {
                        BadgedBox(badge = {
                            if (duplicateCount > 0) {
                                Badge { Text(duplicateCount.toString()) }
                            }
                        }) {
                            Icon(Icons.Filled.Warning, contentDescription = "Duplicates")
                        }
                    }
                    
                    // Sync button (shows loading state)
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
                            Icon(Icons.Filled.Refresh, contentDescription = "Sync Contacts")
                        }
                    }
                    
                    // Add button
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Contact")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No contacts yet")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onSyncContacts) {
                            Text("Sync from Device")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = contacts.filter { !it.isDuplicate },
                        key = { it.id }
                    ) { contact ->
                        ContactListItem(
                            contact = contact,
                            onClick = { onContactClick(contact) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (contact.company.isNotEmpty()) {
                        Text(
                            text = contact.company,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (contact.phones.isNotEmpty()) {
                        Text(
                            text = contact.phones.first(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (contact.emails.isNotEmpty()) {
                        Text(
                            text = contact.emails.first(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                if (contact.isDuplicate) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "DUP",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}