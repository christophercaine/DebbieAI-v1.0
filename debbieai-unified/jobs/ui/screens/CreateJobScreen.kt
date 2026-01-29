package com.debbiedoesit.debbieai.jobs.ui.screens

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
import com.debbiedoesit.debbieai.jobs.data.local.*
import com.debbiedoesit.debbieai.jobs.ui.components.getJobTypeIcon
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    viewModel: JobViewModel,
    onBackClick: () -> Unit,
    onJobCreated: (Long) -> Unit,
    preselectedContactId: Long? = null,
    modifier: Modifier = Modifier
) {
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf(JobType.GENERAL) }
    var priority by remember { mutableStateOf(JobPriority.NORMAL) }
    var estimatedCost by remember { mutableStateOf("") }
    var depositAmount by remember { mutableStateOf("") }
    var estimatedHours by remember { mutableStateOf("") }
    var source by remember { mutableStateOf(LeadSource.DIRECT) }
    var referredBy by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var contactId by remember { mutableStateOf(preselectedContactId) }
    
    // UI state
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showPriorityDropdown by remember { mutableStateOf(false) }
    var showSourceDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Navigate when job created
    LaunchedEffect(uiState.lastCreatedJobId) {
        uiState.lastCreatedJobId?.let { jobId ->
            onJobCreated(jobId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Job") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                isLoading = true
                                viewModel.createJob(
                                    title = title,
                                    contactId = contactId,
                                    jobType = jobType,
                                    description = description,
                                    estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                                    source = source
                                )
                            }
                        },
                        enabled = title.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title (required)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title *") },
                placeholder = { Text("e.g., Kitchen Flooring - Johnson") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank()
            )
            
            // Job Type
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = it }
            ) {
                OutlinedTextField(
                    value = jobType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Job Type") },
                    leadingIcon = { Icon(getJobTypeIcon(jobType), contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false }
                ) {
                    JobType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                jobType = type
                                showTypeDropdown = false
                            },
                            leadingIcon = { Icon(getJobTypeIcon(type), contentDescription = null) }
                        )
                    }
                }
            }
            
            // Priority
            ExposedDropdownMenuBox(
                expanded = showPriorityDropdown,
                onExpandedChange = { showPriorityDropdown = it }
            ) {
                OutlinedTextField(
                    value = priority.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = showPriorityDropdown,
                    onDismissRequest = { showPriorityDropdown = false }
                ) {
                    JobPriority.values().forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.displayName) },
                            onClick = {
                                priority = p
                                showPriorityDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Describe the work to be done...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Divider()
            
            // Financials Section
            Text(
                text = "Financials",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = estimatedCost,
                    onValueChange = { estimatedCost = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Estimated Cost") },
                    leadingIcon = { Text("$") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = depositAmount,
                    onValueChange = { depositAmount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Deposit") },
                    leadingIcon = { Text("$") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            
            OutlinedTextField(
                value = estimatedHours,
                onValueChange = { estimatedHours = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Estimated Hours") },
                trailingIcon = { Text("hrs") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Divider()
            
            // Lead Source Section
            Text(
                text = "Lead Source",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            ExposedDropdownMenuBox(
                expanded = showSourceDropdown,
                onExpandedChange = { showSourceDropdown = it }
            ) {
                OutlinedTextField(
                    value = source.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("How did they find you?") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSourceDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = showSourceDropdown,
                    onDismissRequest = { showSourceDropdown = false }
                ) {
                    LeadSource.values().forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.displayName) },
                            onClick = {
                                source = s
                                showSourceDropdown = false
                            }
                        )
                    }
                }
            }
            
            if (source == LeadSource.REFERRAL) {
                OutlinedTextField(
                    value = referredBy,
                    onValueChange = { referredBy = it },
                    label = { Text("Referred By") },
                    placeholder = { Text("Customer name who referred") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            Divider()
            
            // Customer Link
            Text(
                text = "Customer",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Placeholder for customer picker
            OutlinedCard(
                onClick = { /* Open contact picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (contactId != null) Icons.Default.Person else Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (contactId != null) "Customer Selected" else "Link Customer",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (contactId != null) "Tap to change" else "Optional - link to existing contact",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            
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
                label = { Text("Additional Notes") },
                placeholder = { Text("Any special instructions, access codes, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Create button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        isLoading = true
                        viewModel.createJob(
                            title = title,
                            contactId = contactId,
                            jobType = jobType,
                            description = description,
                            estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                            source = source
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Job")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Edit job screen - similar to create but pre-populated
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    jobId: Long,
    viewModel: JobViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var job by remember { mutableStateOf<Job?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load job
    LaunchedEffect(jobId) {
        job = viewModel.getJobById(jobId)
        isLoading = false
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    val currentJob = job
    if (currentJob == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Job not found")
        }
        return
    }
    
    // Form state initialized from job
    var title by remember { mutableStateOf(currentJob.title) }
    var description by remember { mutableStateOf(currentJob.description) }
    var jobType by remember { mutableStateOf(currentJob.jobType) }
    var priority by remember { mutableStateOf(currentJob.priority) }
    var estimatedCost by remember { mutableStateOf(if (currentJob.estimatedCost > 0) currentJob.estimatedCost.toString() else "") }
    var notes by remember { mutableStateOf(currentJob.notes) }
    
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showPriorityDropdown by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Job") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            isSaving = true
                            viewModel.updateJob(
                                currentJob.copy(
                                    title = title,
                                    description = description,
                                    jobType = jobType,
                                    priority = priority,
                                    estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                                    notes = notes
                                )
                            )
                            onSaved()
                        },
                        enabled = title.isNotBlank() && !isSaving
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Job Number (read only)
            if (currentJob.jobNumber.isNotEmpty()) {
                OutlinedTextField(
                    value = currentJob.jobNumber,
                    onValueChange = {},
                    label = { Text("Job Number") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )
            }
            
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Job Type
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = it }
            ) {
                OutlinedTextField(
                    value = jobType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Job Type") },
                    leadingIcon = { Icon(getJobTypeIcon(jobType), contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false }
                ) {
                    JobType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                jobType = type
                                showTypeDropdown = false
                            },
                            leadingIcon = { Icon(getJobTypeIcon(type), contentDescription = null) }
                        )
                    }
                }
            }
            
            // Priority
            ExposedDropdownMenuBox(
                expanded = showPriorityDropdown,
                onExpandedChange = { showPriorityDropdown = it }
            ) {
                OutlinedTextField(
                    value = priority.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = showPriorityDropdown,
                    onDismissRequest = { showPriorityDropdown = false }
                ) {
                    JobPriority.values().forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.displayName) },
                            onClick = {
                                priority = p
                                showPriorityDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Estimated Cost
            OutlinedTextField(
                value = estimatedCost,
                onValueChange = { estimatedCost = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Estimated Cost") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            // Notes
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
                    isSaving = true
                    viewModel.updateJob(
                        currentJob.copy(
                            title = title,
                            description = description,
                            jobType = jobType,
                            priority = priority,
                            estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                            notes = notes
                        )
                    )
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && !isSaving
            ) {
                Text("Save Changes")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
