package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    viewModel: ContactViewModel,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf(contact.name) }
    var phones by remember { mutableStateOf(contact.phones.ifEmpty { listOf("") }) }
    var emails by remember { mutableStateOf(contact.emails.ifEmpty { listOf("") }) }
    var company by remember { mutableStateOf(contact.company) }
    var jobTitle by remember { mutableStateOf(contact.jobTitle) }
    var linkedIn by remember { mutableStateOf(contact.socialMedia["linkedin"] ?: "") }
    var twitter by remember { mutableStateOf(contact.socialMedia["twitter"] ?: "") }
    var notes by remember { mutableStateOf(contact.notes) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
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
                title = { Text("Edit Contact") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Contact",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(
                        onClick = {
                            val updatedSocialMedia = mutableMapOf<String, String>()
                            if (linkedIn.isNotBlank()) updatedSocialMedia["linkedin"] = linkedIn
                            if (twitter.isNotBlank()) updatedSocialMedia["twitter"] = twitter
                            
                            val updated = contact.copy(
                                name = name,
                                phones = phones.filter { it.isNotBlank() },
                                emails = emails.filter { it.isNotBlank() },
                                company = company,
                                jobTitle = jobTitle,
                                socialMedia = updatedSocialMedia,
                                notes = notes
                            )
                            viewModel.updateContact(updated)
                            onBackClick()
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status badges
            if (contact.isDuplicate || contact.isPlaceholder) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (contact.isDuplicate) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Duplicate",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    if (contact.isPlaceholder) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Placeholder",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Divider()

            // Phone numbers section
            Text(
                text = "Phone Numbers",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            phones.forEachIndexed { index, phone ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { newValue ->
                            phones = phones.toMutableList().apply { this[index] = newValue }
                        },
                        label = { Text("Phone ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    
                    if (phones.size > 1) {
                        IconButton(
                            onClick = {
                                phones = phones.toMutableList().apply { removeAt(index) }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove phone",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            TextButton(onClick = { phones = phones + "" }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Phone")
            }

            Divider()

            // Email addresses section
            Text(
                text = "Email Addresses",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            emails.forEachIndexed { index, email ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newValue ->
                            emails = emails.toMutableList().apply { this[index] = newValue }
                        },
                        label = { Text("Email ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    if (emails.size > 1) {
                        IconButton(
                            onClick = {
                                emails = emails.toMutableList().apply { removeAt(index) }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove email",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            TextButton(onClick = { emails = emails + "" }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Email")
            }

            Divider()

            // Work info
            Text(
                text = "Work",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Divider()

            // Social media
            Text(
                text = "Social Media",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = linkedIn,
                onValueChange = { linkedIn = it },
                label = { Text("LinkedIn URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            OutlinedTextField(
                value = twitter,
                onValueChange = { twitter = it },
                label = { Text("Twitter Handle") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Divider()

            // Notes
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    val updatedSocialMedia = mutableMapOf<String, String>()
                    if (linkedIn.isNotBlank()) updatedSocialMedia["linkedin"] = linkedIn
                    if (twitter.isNotBlank()) updatedSocialMedia["twitter"] = twitter
                    
                    val updated = contact.copy(
                        name = name,
                        phones = phones.filter { it.isNotBlank() },
                        emails = emails.filter { it.isNotBlank() },
                        company = company,
                        jobTitle = jobTitle,
                        socialMedia = updatedSocialMedia,
                        notes = notes
                    )
                    viewModel.updateContact(updated)
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Changes")
            }

            // Delete button
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Contact")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}