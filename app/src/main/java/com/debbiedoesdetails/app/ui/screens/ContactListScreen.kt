package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debbiedoesdetails.app.R
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.viewmodel.ContactViewModel
import com.debbiedoesdetails.app.viewmodel.SyncState

private val DebbieBlue = Color(0xFF1E5AA8)
private val DebbieRed = Color(0xFFD32F2F)
private val DebbieWhite = Color(0xFFFFFFFF)
private val DebbieGold = Color(0xFFFFB300)

private val TypeColors = mapOf(
    "Customer" to Color(0xFF4CAF50),
    "Lead" to Color(0xFFFF9800),
    "Vendor" to Color(0xFF9C27B0),
    "Subcontractor" to Color(0xFF2196F3),
    "Employee" to Color(0xFF00BCD4),
    "Personal" to Color(0xFF607D8B)
)

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
    val nonDuplicateContacts = contacts.filter { !it.isDuplicate }
    
    var searchQuery by remember { mutableStateOf("") }
    var showDuplicatesInList by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var isSmartSearch by remember { mutableStateOf(true) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val smartSearch: (String, List<Contact>) -> List<Contact> = { query, list ->
        val q = query.lowercase()
        when {
            q.contains("customer") || q.contains("client") -> list.filter { it.contactType.equals("Customer", true) }
            q.contains("lead") -> list.filter { it.contactType.equals("Lead", true) }
            q.contains("vendor") || q.contains("supplier") -> list.filter { it.contactType.equals("Vendor", true) }
            q.contains("subcontractor") || q.contains("sub") -> list.filter { it.contactType.equals("Subcontractor", true) }
            q.contains("employee") || q.contains("staff") -> list.filter { it.contactType.equals("Employee", true) }
            q.contains("no email") -> list.filter { it.emails.isEmpty() }
            q.contains("no phone") -> list.filter { it.phones.isEmpty() }
            q.contains("vip") || q.contains("important") -> list.filter { it.notes.lowercase().contains("vip") || it.notes.lowercase().contains("important") }
            q.startsWith("in ") -> { val loc = q.substringAfter("in ").trim(); list.filter { it.city.lowercase().contains(loc) || it.state.lowercase().contains(loc) } }
            q.startsWith("from ") || q.startsWith("at ") -> { val co = q.substringAfter(if (q.startsWith("from ")) "from " else "at ").trim(); list.filter { it.company.lowercase().contains(co) } }
            q.contains("recent") || q.contains("new") -> list.sortedByDescending { it.createdAt }.take(20)
            q.contains("duplicate") -> list.filter { it.isDuplicate }
            else -> list.filter { c -> c.name.lowercase().contains(q) || c.company.lowercase().contains(q) || c.phones.any { it.contains(q) } || c.emails.any { it.lowercase().contains(q) } || c.notes.lowercase().contains(q) }
        }
    }
    
    val displayContacts = when {
        searchQuery.isNotBlank() -> if (isSmartSearch) smartSearch(searchQuery, contacts) else contacts.filter { it.name.lowercase().contains(searchQuery.lowercase()) || it.company.lowercase().contains(searchQuery.lowercase()) }
        selectedType != null -> contacts.filter { it.contactType == selectedType && !it.isDuplicate }
        showDuplicatesInList -> contacts
        else -> nonDuplicateContacts
    }.sortedBy { it.name.lowercase() }
    
    val contactTypes = contacts.filter { !it.isDuplicate }.groupBy { it.contactType }.mapValues { it.value.size }.filter { it.value > 0 }
    
    LaunchedEffect(syncState) {
        when (val state = syncState) {
            is SyncState.Success -> { snackbarHostState.showSnackbar("Synced: ${state.result.added} added, ${state.result.updated} updated"); viewModel.resetSyncState() }
            is SyncState.Error -> { snackbarHostState.showSnackbar("Sync failed: ${state.message}"); viewModel.resetSyncState() }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            Column {
                Box(modifier = Modifier.fillMaxWidth().background(DebbieWhite).padding(top = 32.dp, bottom = 8.dp)) {
                    Image(painter = painterResource(id = R.drawable.debdoesconlogo), contentDescription = "Debbie Does Contacts", modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Fit)
                }
                Surface(color = DebbieBlue, shadowElevation = 4.dp) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${if (showDuplicatesInList) contacts.size else nonDuplicateContacts.size} contacts", color = DebbieWhite, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
                        Row {
                            IconButton(onClick = onDuplicatesClick) { BadgedBox(badge = { if (duplicateCount > 0) Badge(containerColor = DebbieRed, contentColor = DebbieWhite) { Text(duplicateCount.toString()) } }) { Icon(Icons.Filled.Warning, contentDescription = "Duplicates", tint = if (duplicateCount > 0) DebbieRed else DebbieWhite.copy(alpha = 0.7f)) } }
                            IconButton(onClick = onSyncContacts, enabled = syncState !is SyncState.Syncing) { if (syncState is SyncState.Syncing) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = DebbieWhite) else Icon(Icons.Filled.Refresh, contentDescription = "Sync", tint = DebbieWhite) }
                            IconButton(onClick = onAddClick) { Icon(Icons.Filled.Add, contentDescription = "Add", tint = DebbieWhite) }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.weight(1f), placeholder = { Text(if (isSmartSearch) "Try: \"customers\" or \"in Leesburg\"" else "Search...") }, leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = DebbieBlue) }, trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = DebbieRed) } }, singleLine = true, shape = MaterialTheme.shapes.medium, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DebbieBlue, unfocusedBorderColor = Color.LightGray, focusedContainerColor = DebbieWhite, unfocusedContainerColor = DebbieWhite))
                IconButton(onClick = { isSmartSearch = !isSmartSearch }, modifier = Modifier.padding(start = 4.dp)) { Icon(Icons.Filled.Star, contentDescription = "Smart Search", tint = if (isSmartSearch) DebbieGold else Color.Gray) }
            }
            if (contactTypes.isNotEmpty()) {
                LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(selected = selectedType == null, onClick = { selectedType = null }, label = { Text("All") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DebbieBlue, selectedLabelColor = DebbieWhite)) }
                    items(contactTypes.keys.toList()) { type -> val count = contactTypes[type] ?: 0; FilterChip(selected = selectedType == type, onClick = { selectedType = if (selectedType == type) null else type }, label = { Text("$type ($count)") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TypeColors[type] ?: DebbieBlue, selectedLabelColor = DebbieWhite)) }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (searchQuery.isBlank() && selectedType == null && duplicateCount > 0) { Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Show duplicates", color = Color.Gray); Switch(checked = showDuplicatesInList, onCheckedChange = { showDuplicatesInList = it }, colors = SwitchDefaults.colors(checkedThumbColor = DebbieWhite, checkedTrackColor = DebbieBlue)) }; Divider(modifier = Modifier.padding(horizontal = 12.dp), color = Color.LightGray) }
            if (searchQuery.isNotBlank()) { Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) { if (isSmartSearch) { Icon(Icons.Filled.Star, contentDescription = null, tint = DebbieGold, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text("Smart: ", style = MaterialTheme.typography.bodySmall, color = DebbieGold) }; Text("${displayContacts.size} results", style = MaterialTheme.typography.bodySmall, color = Color.Gray) } }
            if (contacts.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray); Spacer(modifier = Modifier.height(16.dp)); Text("No contacts yet", style = MaterialTheme.typography.titleMedium, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp)); Button(onClick = onSyncContacts, colors = ButtonDefaults.buttonColors(containerColor = DebbieBlue)) { Icon(Icons.Filled.Refresh, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Sync from Device") } } } }
            else if (displayContacts.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray); Spacer(modifier = Modifier.height(16.dp)); Text("No contacts found", style = MaterialTheme.typography.titleMedium, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp)); TextButton(onClick = { searchQuery = ""; selectedType = null }) { Text("Clear filters", color = DebbieBlue) } } } }
            else { LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) { items(items = displayContacts, key = { it.id }) { contact -> ContactListItem(contact = contact, onClick = { onContactClick(contact) }) } } }
        }
    }
}

@Composable
fun ContactListItem(contact: Contact, onClick: () -> Unit) {
    val typeColor = TypeColors[contact.contactType] ?: Color.Gray
    val isVIP = contact.notes.lowercase().contains("vip") || contact.notes.lowercase().contains("important")
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = DebbieWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box { Surface(modifier = Modifier.size(52.dp), shape = MaterialTheme.shapes.extraLarge, color = typeColor.copy(alpha = 0.2f)) { Box(contentAlignment = Alignment.Center) { Surface(modifier = Modifier.size(46.dp), shape = MaterialTheme.shapes.extraLarge, color = DebbieBlue) { Box(contentAlignment = Alignment.Center) { Text(text = contact.name.firstOrNull()?.uppercase() ?: "?", color = DebbieWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp) } } } } }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Text(text = contact.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black, modifier = Modifier.weight(1f, fill = false)); if (isVIP) { Spacer(modifier = Modifier.width(4.dp)); Icon(Icons.Filled.Star, contentDescription = "VIP", tint = DebbieGold, modifier = Modifier.size(16.dp)) } }
                if (contact.company.isNotEmpty()) { Text(text = contact.company, style = MaterialTheme.typography.bodySmall, color = DebbieBlue, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                if (contact.phones.isNotEmpty()) { Text(text = contact.phones.first(), style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1) }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(color = typeColor, shape = MaterialTheme.shapes.small) { Text(text = contact.contactType.take(4).uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = DebbieWhite, fontWeight = FontWeight.Bold) }
                if (contact.isDuplicate) { Surface(color = DebbieRed.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) { Text(text = "DUP", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = DebbieRed, fontWeight = FontWeight.Bold) } }
                if (contact.isPlaceholder) { Surface(color = Color.Gray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) { Text(text = "TEMP", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray) } }
            }
        }
    }
}
