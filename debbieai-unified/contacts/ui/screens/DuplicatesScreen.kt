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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.ui.components.ContactAvatar
import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel
import com.debbiedoesit.debbieai.core.ui.theme.DebbieBlue
import com.debbiedoesit.debbieai.core.ui.theme.DebbieGreen
import com.debbiedoesit.debbieai.core.ui.theme.DebbieRed

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
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = DebbieGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = duplicates,
                    key = { it.id }
                ) { duplicate ->
                    val originalContact = contacts.find { it.id == duplicate.isDuplicateOf }
                    
                    DuplicatePairCard(
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
fun DuplicatePairCard(
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
            // Original contact
            if (original != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = DebbieGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Original",
                        style = MaterialTheme.typography.labelMedium,
                        color = DebbieGreen
                    )
                }
                ContactCompactInfo(contact = original)
                
                HorizontalDivider()
            }

            // Duplicate contact
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = DebbieRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (original != null) "Possible Duplicate" else "Duplicate (original not found)",
                    style = MaterialTheme.typography.labelMedium,
                    color = DebbieRed
                )
            }
            ContactCompactInfo(contact = duplicate)

            HorizontalDivider()

            // Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (original != null) {
                        Button(
                            onClick = onMerge,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DebbieBlue
                            )
                        ) {
                            Icon(Icons.Filled.Merge, contentDescription = null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Merge")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onDeleteDuplicate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DebbieRed
                        )
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
                
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
fun ContactCompactInfo(contact: Contact) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = contact.name,
                photoUri = contact.photoUri,
                contactType = contact.contactType,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (contact.phones.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contact.phones.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                if (contact.emails.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contact.emails.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                if (contact.company.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Business,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contact.company,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
