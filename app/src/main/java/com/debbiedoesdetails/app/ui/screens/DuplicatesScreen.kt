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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Potential Duplicates (${duplicates.size})") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (duplicates.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No duplicates found!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(duplicates) { duplicate ->
                    val originalContact = contacts.find { it.id == duplicate.duplicateOf }
                    if (originalContact != null) {
                        DuplicatePair(
                            original = originalContact,
                            duplicate = duplicate,
                            onMerge = {
                                viewModel.mergeContacts(originalContact, duplicate)
                                onBackClick()
                            },
                            onDeleteDuplicate = {
                                viewModel.deleteContact(duplicate.id)
                                onBackClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicatePair(
    original: Contact,
    duplicate: Contact,
    onMerge: () -> Unit,
    onDeleteDuplicate: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Original Contact", style = MaterialTheme.typography.titleSmall)
            ContactInfoCard(contact = original)

            Divider()

            Text("Possible Duplicate", style = MaterialTheme.typography.titleSmall)
            ContactInfoCard(contact = duplicate)

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMerge,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Keep Original")
                }
                OutlinedButton(
                    onClick = onDeleteDuplicate,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete Duplicate")
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
            Text(contact.name, style = MaterialTheme.typography.titleSmall)
            if (contact.email.isNotEmpty()) {
                Text("📧 ${contact.email}", style = MaterialTheme.typography.bodySmall)
            }
            if (contact.phone.isNotEmpty()) {
                Text("📱 ${contact.phone}", style = MaterialTheme.typography.bodySmall)
            }
            if (contact.company.isNotEmpty()) {
                Text("🏢 ${contact.company}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}