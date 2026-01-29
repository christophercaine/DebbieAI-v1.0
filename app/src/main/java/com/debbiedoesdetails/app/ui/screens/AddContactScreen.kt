package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    viewModel: ContactViewModel,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // Multiple phones and emails
    var phones by remember { mutableStateOf(listOf("")) }
    var emails by remember { mutableStateOf(listOf("")) }
    
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Contact") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                isLoading = true
                                viewModel.addContact(
                                    name = name,
                                    phones = phones.filter { it.isNotBlank() },
                                    emails = emails.filter { it.isNotBlank() },
                                    company = company,
                                    jobTitle = jobTitle,
                                    notes = notes
                                )
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
            // Name (required)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
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
                            phones = phones.toMutableList().apply { 
                                this[index] = newValue 
                            }
                        },
                        label = { Text("Phone ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        singleLine = true
                    )
                    
                    if (phones.size > 1) {
                        IconButton(
                            onClick = {
                                phones = phones.toMutableList().apply { 
                                    removeAt(index) 
                                }
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
            
            TextButton(
                onClick = { phones = phones + "" }
            ) {
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
                            emails = emails.toMutableList().apply { 
                                this[index] = newValue 
                            }
                        },
                        label = { Text("Email ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        singleLine = true
                    )
                    
                    if (emails.size > 1) {
                        IconButton(
                            onClick = {
                                emails = emails.toMutableList().apply { 
                                    removeAt(index) 
                                }
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
            
            TextButton(
                onClick = { emails = emails + "" }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Email")
            }

            Divider()

            // Company info
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
                enabled = !isLoading,
                singleLine = true
            )
            
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
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
                enabled = !isLoading,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        isLoading = true
                        viewModel.addContact(
                            name = name,
                            phones = phones.filter { it.isNotBlank() },
                            emails = emails.filter { it.isNotBlank() },
                            company = company,
                            jobTitle = jobTitle,
                            notes = notes
                        )
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
                    Text("Save Contact")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}