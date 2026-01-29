package com.debbiedoesdetails.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
<<<<<<< HEAD
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
=======
import androidx.compose.foundation.rememberScrollState
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Star
=======
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

private val DebbieBlue = Color(0xFF1E5AA8)
private val DebbieRed = Color(0xFFD32F2F)
private val DebbieGold = Color(0xFFFFB300)

private val ContactTypes = listOf("Personal", "Customer", "Lead", "Vendor", "Subcontractor", "Employee")
private val QuickTags = listOf("VIP", "Follow Up", "Urgent", "Pending", "Paid", "Referral", "Callback", "Estimate Sent", "New Lead", "Hot Lead", "Cold Lead", "Do Not Contact")

private val TagColors = mapOf(
    "VIP" to Color(0xFFFFB300),
    "Follow Up" to Color(0xFFFF9800),
    "Urgent" to Color(0xFFD32F2F),
    "Pending" to Color(0xFF9C27B0),
    "Paid" to Color(0xFF4CAF50),
    "Referral" to Color(0xFF2196F3),
    "Callback" to Color(0xFFFF5722),
    "Estimate Sent" to Color(0xFF00BCD4),
    "New Lead" to Color(0xFF8BC34A),
    "Hot Lead" to Color(0xFFE91E63),
    "Cold Lead" to Color(0xFF607D8B),
    "Do Not Contact" to Color(0xFF795548)
)

private fun getTagColor(tag: String): Color {
    return TagColors[tag] ?: Color(0xFF1E5AA8)
}

private fun autoDetectType(company: String, jobTitle: String, notes: String): String {
    val c = company.lowercase()
    val j = jobTitle.lowercase()
    val n = notes.lowercase()
    return when {
        n.contains("quote") || n.contains("estimate") || n.contains("interested") || n.contains("lead") -> "Lead"
        n.contains("customer") || n.contains("client") || n.contains("job") || n.contains("project") || n.contains("paid") || n.contains("invoice") -> "Customer"
        c.contains("supply") || c.contains("lumber") || c.contains("hardware") || c.contains("wholesale") || c.contains("depot") || n.contains("vendor") || n.contains("supplier") -> "Vendor"
        j.contains("plumber") || j.contains("electrician") || j.contains("hvac") || j.contains("painter") || j.contains("roofer") || c.contains("plumbing") || c.contains("electric") || c.contains("roofing") || n.contains("sub") -> "Subcontractor"
        j.contains("employee") || j.contains("worker") || j.contains("foreman") || j.contains("crew") || n.contains("employee") || n.contains("staff") -> "Employee"
        else -> "Personal"
    }
}

private fun autoDetectTags(notes: String): List<String> {
    val n = notes.lowercase()
    val tags = mutableListOf<String>()
    if (n.contains("vip") || n.contains("important") || n.contains("priority") || n.contains("top client")) tags.add("VIP")
    if (n.contains("follow up") || n.contains("follow-up") || n.contains("followup") || n.contains("check back") || n.contains("touch base")) tags.add("Follow Up")
    if (n.contains("urgent") || n.contains("asap") || n.contains("rush") || n.contains("emergency") || n.contains("immediately")) tags.add("Urgent")
    if (n.contains("pending") || n.contains("waiting") || n.contains("on hold")) tags.add("Pending")
    if (n.contains("paid") || n.contains("payment received") || n.contains("invoice paid")) tags.add("Paid")
    if (n.contains("referral") || n.contains("referred by") || n.contains("recommended by")) tags.add("Referral")
    if (n.contains("callback") || n.contains("call back") || n.contains("call me") || n.contains("return call") || n.contains("needs call")) tags.add("Callback")
    if (n.contains("estimate sent") || n.contains("quote sent") || n.contains("bid sent") || n.contains("sent estimate") || n.contains("sent quote")) tags.add("Estimate Sent")
    if (n.contains("new lead") || n.contains("just contacted") || n.contains("first contact")) tags.add("New Lead")
    if (n.contains("hot lead") || n.contains("very interested") || n.contains("ready to buy") || n.contains("ready to sign")) tags.add("Hot Lead")
    if (n.contains("cold lead") || n.contains("not interested") || n.contains("maybe later")) tags.add("Cold Lead")
    if (n.contains("do not contact") || n.contains("don't contact") || n.contains("no contact") || n.contains("blocked")) tags.add("Do Not Contact")
    return tags
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(viewModel: ContactViewModel, onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
<<<<<<< HEAD
    var phones by remember { mutableStateOf(listOf("")) }
    var emails by remember { mutableStateOf(listOf("")) }
    var contactType by remember { mutableStateOf("Personal") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var customTagInput by remember { mutableStateOf("") }
=======
    
    // Multiple phones and emails
    var phones by remember { mutableStateOf(listOf("")) }
    var emails by remember { mutableStateOf(listOf("")) }
    
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
    var isLoading by remember { mutableStateOf(false) }
    var showAutoDetect by remember { mutableStateOf(false) }

    val detectedType = autoDetectType(company, jobTitle, notes)
    val detectedTags = autoDetectTags(notes)
    
    LaunchedEffect(company, jobTitle, notes) {
        showAutoDetect = detectedType != "Personal" || detectedTags.isNotEmpty()
    }

    fun saveContact() {
        if (name.isNotBlank()) {
            isLoading = true
            val tagText = if (selectedTags.isNotEmpty()) selectedTags.joinToString(", ") { "[$it]" } else ""
            val typeText = if (contactType != "Personal") "[Type:$contactType]" else ""
            val finalNotes = listOf(notes.trim(), tagText, typeText).filter { it.isNotEmpty() }.joinToString("\n")
            viewModel.addContact(name = name, phones = phones.filter { it.isNotBlank() }, emails = emails.filter { it.isNotBlank() }, company = company, jobTitle = jobTitle, notes = finalNotes)
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Contact") },
<<<<<<< HEAD
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                actions = { TextButton(onClick = { saveContact() }, enabled = name.isNotBlank() && !isLoading) { Text("Save", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DebbieBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            
            // Contact Type
            Text("Contact Type", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ContactTypes) { type ->
                    FilterChip(selected = contactType == type, onClick = { contactType = type }, label = { Text(type) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DebbieBlue, selectedLabelColor = Color.White))
                }
            }
            
            // AI Suggestion
            if (showAutoDetect) {
                Card(colors = CardDefaults.cardColors(containerColor = DebbieGold.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = DebbieGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Detected", fontWeight = FontWeight.Bold, color = DebbieGold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (detectedType != "Personal" && detectedType != contactType) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Text("Type: ", style = MaterialTheme.typography.bodySmall)
                                Surface(color = DebbieBlue.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                    Text(detectedType, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { contactType = detectedType }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Apply", color = DebbieBlue, fontWeight = FontWeight.Bold) }
                            }
                        }
                        val newTags = detectedTags.filter { it !in selectedTags }
                        if (newTags.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Text("Tags: ", style = MaterialTheme.typography.bodySmall)
                                newTags.take(3).forEach { tag ->
                                    Surface(color = getTagColor(tag).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(end = 4.dp)) {
                                        Text(tag, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.bodySmall, color = getTagColor(tag), fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (newTags.size > 3) Text("+${newTags.size - 3}", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { selectedTags = selectedTags + newTags }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Apply All", color = DebbieBlue, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }
            
            Divider()
            
            // Selected Tags Display
            if (selectedTags.isNotEmpty()) {
                Text("Applied Tags", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedTags.toList()) { tag ->
                        InputChip(
                            selected = true,
                            onClick = { selectedTags = selectedTags - tag },
                            label = { Text(tag) },
                            trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) },
                            colors = InputChipDefaults.inputChipColors(selectedContainerColor = getTagColor(tag), selectedLabelColor = Color.White, selectedTrailingIconColor = Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Quick Tags
            Text("Quick Tags", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(QuickTags.filter { it !in selectedTags }) { tag ->
                    SuggestionChip(onClick = { selectedTags = selectedTags + tag }, label = { Text(tag, style = MaterialTheme.typography.bodySmall) }, colors = SuggestionChipDefaults.suggestionChipColors(containerColor = getTagColor(tag).copy(alpha = 0.15f), labelColor = getTagColor(tag)))
                }
            }
            
            // Custom Tag Input
            Text("Add Custom Tag", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = customTagInput,
                    onValueChange = { customTagInput = it },
                    label = { Text("Type a tag name...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (customTagInput.isNotBlank()) {
                            selectedTags = selectedTags + customTagInput.trim()
                            customTagInput = ""
                        }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (customTagInput.isNotBlank()) {
                            selectedTags = selectedTags + customTagInput.trim()
                            customTagInput = ""
                        }
                    },
                    enabled = customTagInput.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = DebbieBlue)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Tag", tint = Color.White)
                }
            }
            
            Divider()
            
            // Phone Numbers
            Text("Phone Numbers", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            phones.forEachIndexed { index, phone ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = phone, onValueChange = { newValue -> phones = phones.toMutableList().apply { this[index] = newValue } }, label = { Text("Phone ${index + 1}") }, modifier = Modifier.weight(1f), singleLine = true)
                    if (phones.size > 1) { IconButton(onClick = { phones = phones.toMutableList().apply { removeAt(index) } }) { Icon(Icons.Filled.Close, contentDescription = "Remove", tint = DebbieRed) } }
                }
            }
            TextButton(onClick = { phones = phones + "" }) { Icon(Icons.Filled.Add, contentDescription = null); Spacer(modifier = Modifier.width(4.dp)); Text("Add Phone") }
            
            Divider()
            
            // Emails
            Text("Email Addresses", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            emails.forEachIndexed { index, email ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = email, onValueChange = { newValue -> emails = emails.toMutableList().apply { this[index] = newValue } }, label = { Text("Email ${index + 1}") }, modifier = Modifier.weight(1f), singleLine = true)
                    if (emails.size > 1) { IconButton(onClick = { emails = emails.toMutableList().apply { removeAt(index) } }) { Icon(Icons.Filled.Close, contentDescription = "Remove", tint = DebbieRed) } }
                }
            }
            TextButton(onClick = { emails = emails + "" }) { Icon(Icons.Filled.Add, contentDescription = null); Spacer(modifier = Modifier.width(4.dp)); Text("Add Email") }
            
            Divider()
            
            // Work
            Text("Work", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            
            Divider()
            
            // Notes
            Text("Notes", style = MaterialTheme.typography.titleSmall, color = DebbieBlue, fontWeight = FontWeight.Bold)
            Text("Tip: Type keywords like 'VIP', 'urgent', 'follow up' to auto-detect tags", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { saveContact() }, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = name.isNotBlank() && !isLoading, colors = ButtonDefaults.buttonColors(containerColor = DebbieBlue)) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White) else Text("Save Contact")
            }
            
=======
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
            
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
