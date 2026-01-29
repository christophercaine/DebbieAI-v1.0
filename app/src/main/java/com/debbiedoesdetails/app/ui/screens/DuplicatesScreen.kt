package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicatesScreen(
    viewModel: ContactViewModel,
    onBackClick: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState(initial = emptyList())
    val duplicates = contacts.filter { it.isDuplicate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Potential Duplicates (${duplicates.size})") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        
        if (duplicates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No duplicates found!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All your contacts are unique",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = duplicates,
                    key = { it.id }
                ) { duplicate ->
                    val originalContact = contacts.find { it.id == duplicate.isDuplicateOf }
                    
                    DuplicatePair(
                        original = originalContact,
                        duplicate = duplicate,
                        onMerge = {
                            if (originalContact != null) {
                                viewModel.mergeContacts(originalContact.id, duplicate.id)
                            }
                        },
                        onKeepBoth = {
                            viewModel.dismissDuplicate(duplicate.id)
                        },
                        onDeleteDuplicate = {
                            viewModel.deleteContact(duplicate.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DuplicatePair(
    original: Contact?,
    duplicate: Contact,
    onMerge: () -> Unit,
    onKeepBoth: () -> Unit,
    onDeleteDuplicate: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Original contact (if found)
            if (original != null) {
                Text(
                    text = "Original",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                ContactInfoCard(contact = original)
                
                Divider()
            }

            // Duplicate contact
            Text(
                text = if (original != null) "Possible Duplicate" else "Duplicate (original not found)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
            ContactInfoCard(contact = duplicate)

            Divider()

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (original != null) {
                        Button(
                            onClick = onMerge,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Merge")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onDeleteDuplicate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
                
                // Secondary action
                TextButton(
                    onClick = onKeepBoth,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Not a duplicate - Keep both")
                }
            }
        }
    }
}

@Composable
fun ContactInfoCard(contact: Contact) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (contact.phones.isNotEmpty()) {
                contact.phones.forEach { phone ->
                    Text(
                        text = "📱 $phone",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (contact.emails.isNotEmpty()) {
                contact.emails.forEach { email ->
                    Text(
                        text = "📧 $email",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (contact.company.isNotEmpty()) {
                Text(
                    text = "🏢 ${contact.company}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}