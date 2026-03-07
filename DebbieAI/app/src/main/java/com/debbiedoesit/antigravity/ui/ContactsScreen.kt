package com.debbiedoesit.antigravity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(onBack: () -> Unit = {}) {
    val mockContacts =
            listOf(
                    Contact("John Smith", "Project: Kitchen Remodel", "Active"),
                    Contact("Sarah Jones", "Project: Deck Repair", "Follow-up"),
                    Contact("Mike Wilson", "Project: Bathroom Tile", "Lead")
            )

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Debbie CRM", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color(0xFF1A1A2E)
                                )
                )
            },
            containerColor = Color(0xFF121212),
            floatingActionButton = {
                FloatingActionButton(onClick = {}, containerColor = Color(0xFF2196F3)) {
                    Icon(Icons.Default.Add, contentDescription = "Add Contact", tint = Color.White)
                }
            }
    ) { padding ->
        LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { items(mockContacts) { contact -> ContactCard(contact) } }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
            shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                    modifier =
                            Modifier.size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2196F3).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
            ) {
                Text(contact.name.take(1), color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(contact.description, color = Color.Gray, fontSize = 12.sp)
            }
            Surface(
                    shape = RoundedCornerShape(4.dp),
                    color =
                            when (contact.status) {
                                "Active" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                "Follow-up" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                else -> Color(0xFF2196F3).copy(alpha = 0.2f)
                            }
            ) {
                Text(
                        contact.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color =
                                when (contact.status) {
                                    "Active" -> Color(0xFF81C784)
                                    "Follow-up" -> Color(0xFFFFB74D)
                                    else -> Color(0xFF64B5F6)
                                },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class Contact(val name: String, val description: String, val status: String)
