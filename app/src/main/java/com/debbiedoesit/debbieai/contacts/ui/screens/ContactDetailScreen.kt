package com.debbiedoesit.debbieai.contacts.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.data.local.ContactType
import com.debbiedoesit.debbieai.contacts.ui.components.ContactAvatar
import com.debbiedoesit.debbieai.contacts.ui.components.ContactTypeBadge
import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel
import com.debbiedoesit.debbieai.core.ui.theme.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    viewModel: ContactViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Contact") },
            text = { Text("Are you sure you want to delete ${contact.name}? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteContact(contact.id)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(contact.id) }) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (contact.isFavorite) DebbieOrange else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
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
        
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ContactAvatar(
                        name = contact.name,
                        photoUri = contact.photoUri,
                        contactType = contact.contactType,
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (contact.company.isNotEmpty()) {
                        Text(
                            text = contact.company,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (contact.jobTitle.isNotEmpty()) {
                        Text(
                            text = contact.jobTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ContactTypeBadge(type = contact.contactType)
                    
                    // Tags
                    if (contact.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            contact.tags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (contact.phones.isNotEmpty()) {
                    QuickActionButton(
                        icon = Icons.Filled.Phone,
                        label = "Call",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.phones.first()}")
                            }
                            context.startActivity(intent)
                            viewModel.recordContact(contact.id)
                        }
                    )
                    
                    QuickActionButton(
                        icon = Icons.Filled.Sms,
                        label = "Text",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("sms:${contact.phones.first()}")
                            }
                            context.startActivity(intent)
                            viewModel.recordContact(contact.id)
                        }
                    )
                }
                
                if (contact.emails.isNotEmpty()) {
                    QuickActionButton(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${contact.emails.first()}")
                            }
                            context.startActivity(intent)
                            viewModel.recordContact(contact.id)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Phone numbers
            if (contact.phones.isNotEmpty()) {
                SectionCard(
                    title = "Phone Numbers",
                    icon = Icons.Filled.Phone
                ) {
                    contact.phones.forEach { phone ->
                        ContactInfoRow(
                            icon = Icons.Filled.Phone,
                            value = phone,
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phone")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            // Email addresses
            if (contact.emails.isNotEmpty()) {
                SectionCard(
                    title = "Email Addresses",
                    icon = Icons.Filled.Email
                ) {
                    contact.emails.forEach { email ->
                        ContactInfoRow(
                            icon = Icons.Filled.Email,
                            value = email,
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$email")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            // Notes
            if (contact.notes.isNotEmpty()) {
                SectionCard(
                    title = "Notes",
                    icon = Icons.Filled.Notes
                ) {
                    Text(
                        text = contact.notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Timestamps
            SectionCard(
                title = "Activity",
                icon = Icons.Filled.History
            ) {
                val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                
                if (contact.lastContactedAt != null) {
                    InfoRow(
                        label = "Last contacted",
                        value = contact.lastContactedAt.format(dateFormatter)
                    )
                }
                InfoRow(
                    label = "Created",
                    value = contact.createdAt.format(dateFormatter)
                )
                InfoRow(
                    label = "Updated",
                    value = contact.updatedAt.format(dateFormatter)
                )
                if (contact.syncedAt != null) {
                    InfoRow(
                        label = "Last synced",
                        value = contact.syncedAt.format(dateFormatter)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DebbieBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = DebbieBlue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = "Action",
                tint = DebbieBlue
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
