package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val phones = remember { mutableStateListOf(*contact.phones.toTypedArray()) }
    val emails = remember { mutableStateListOf(*contact.emails.toTypedArray()) }
    var company by remember { mutableStateOf(contact.company) }
    var jobTitle by remember { mutableStateOf(contact.jobTitle) }
    var linkedIn by remember { mutableStateOf(contact.linkedIn) }
    var twitter by remember { mutableStateOf(contact.twitter) }
    var notes by remember { mutableStateOf(contact.notes) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Edit Contact") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Basic Information", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Phone Numbers", style = MaterialTheme.typography.titleSmall)
            phones.forEachIndexed { index, phone ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phones[index] = it },
                        label = { Text("Phone ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    IconButton(
                        onClick = { phones.removeAt(index) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
            Button(
                onClick = { phones.add("") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
                Text("Add Phone")
            }

            Text("Email Addresses", style = MaterialTheme.typography.titleSmall)
            emails.forEachIndexed { index, email ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { emails[index] = it },
                        label = { Text("Email ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    IconButton(
                        onClick = { emails.removeAt(index) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
            Button(
                onClick = { emails.add("") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
                Text("Add Email")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Professional Info", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = linkedIn,
                onValueChange = { linkedIn = it },
                label = { Text("LinkedIn URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            OutlinedTextField(
                value = twitter,
                onValueChange = { twitter = it },
                label = { Text("Twitter Handle") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (contact.isPlaceholder) {
                Text("Placeholder Contact", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            if (contact.isDuplicate) {
                Text("Duplicate Detected", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                Text("This contact may be a duplicate. Check and merge if needed.", style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    val updated = contact.copy(
                        name = name,
                        phones = phones.filter { it.isNotEmpty() },
                        emails = emails.filter { it.isNotEmpty() },
                        company = company,
                        jobTitle = jobTitle,
                        linkedIn = linkedIn,
                        twitter = twitter,
                        notes = notes
                    )
                    viewModel.updateContact(updated)
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}