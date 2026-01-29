package com.debbiedoesit.debbieai.contacts.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.data.local.ContactType
import com.debbiedoesit.debbieai.contacts.ui.components.ContactTypeSelector
import com.debbiedoesit.debbieai.contacts.ui.components.QuickTypeChips
import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel
import com.debbiedoesit.debbieai.core.ui.theme.DebbieBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    viewModel: ContactViewModel,
    onBackClick: () -> Unit,
    editContact: Contact? = null  // If provided, we're editing
) {
    val isEditing = editContact != null
    
    // Form state
    var name by remember { mutableStateOf(editContact?.name ?: "") }
    var phones by remember { mutableStateOf(editContact?.phones?.ifEmpty { listOf("") } ?: listOf("")) }
    var emails by remember { mutableStateOf(editContact?.emails?.ifEmpty { listOf("") } ?: listOf("")) }
    var company by remember { mutableStateOf(editContact?.company ?: "") }
    var jobTitle by remember { mutableStateOf(editContact?.jobTitle ?: "") }
    var contactType by remember { mutableStateOf(editContact?.contactType ?: ContactType.CUSTOMER) }
    var notes by remember { mutableStateOf(editContact?.notes ?: "") }
    var tags by remember { mutableStateOf(editContact?.tags ?: emptyList()) }
    var newTag by remember { mutableStateOf("") }
    
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation
    if (showDeleteDialog && editContact != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Contact") },
            text = { Text("Are you sure you want to delete ${editContact.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteContact(editContact.id)
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
                title = { Text(if (isEditing) "Edit Contact" else "Add Contact") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                isLoading = true
                                if (isEditing && editContact != null) {
                                    viewModel.updateContact(
                                        editContact.copy(
                                            name = name,
                                            phones = phones.filter { it.isNotBlank() },
                                            emails = emails.filter { it.isNotBlank() },
                                            company = company,
                                            jobTitle = jobTitle,
                                            contactType = contactType,
                                            notes = notes,
                                            tags = tags
                                        )
                                    )
                                } else {
                                    viewModel.addContact(
                                        name = name,
                                        phones = phones.filter { it.isNotBlank() },
                                        emails = emails.filter { it.isNotBlank() },
                                        company = company,
                                        jobTitle = jobTitle,
                                        notes = notes,
                                        contactType = contactType,
                                        tags = tags
                                    )
                                }
                                onBackClick()
                            }
                        },
                        enabled = name.isNotBlank() && !isLoading
                    ) {
                        Text("Save")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Contact Type
            Text(
                text = "Contact Type",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
            )
            QuickTypeChips(
                selectedType = contactType,
                onTypeSelected = { contactType = it }
            )

            HorizontalDivider()

            // Phone Numbers
            Text(
                text = "Phone Numbers",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
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
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    
                    if (phones.size > 1) {
                        IconButton(
                            onClick = { phones = phones.toMutableList().apply { removeAt(index) } }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
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

            HorizontalDivider()

            // Email Addresses
            Text(
                text = "Email Addresses",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
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
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    if (emails.size > 1) {
                        IconButton(
                            onClick = { emails = emails.toMutableList().apply { removeAt(index) } }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
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

            HorizontalDivider()

            // Work Info
            Text(
                text = "Work",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
            )
            
            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            // Tags
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text("Add tag") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalIconButton(
                    onClick = {
                        if (newTag.isNotBlank() && newTag !in tags) {
                            tags = tags + newTag.trim()
                            newTag = ""
                        }
                    },
                    enabled = newTag.isNotBlank()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add tag")
                }
            }
            
            if (tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        InputChip(
                            selected = false,
                            onClick = { tags = tags - tag },
                            label = { Text(tag) },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            // Notes
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleSmall,
                color = DebbieBlue
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
                    if (name.isNotBlank()) {
                        isLoading = true
                        if (isEditing && editContact != null) {
                            viewModel.updateContact(
                                editContact.copy(
                                    name = name,
                                    phones = phones.filter { it.isNotBlank() },
                                    emails = emails.filter { it.isNotBlank() },
                                    company = company,
                                    jobTitle = jobTitle,
                                    contactType = contactType,
                                    notes = notes,
                                    tags = tags
                                )
                            )
                        } else {
                            viewModel.addContact(
                                name = name,
                                phones = phones.filter { it.isNotBlank() },
                                emails = emails.filter { it.isNotBlank() },
                                company = company,
                                jobTitle = jobTitle,
                                notes = notes,
                                contactType = contactType,
                                tags = tags
                            )
                        }
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = name.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditing) "Save Changes" else "Save Contact")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
