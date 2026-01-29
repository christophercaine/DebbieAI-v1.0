package com.debbiedoesit.debbieai.jobs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.jobs.data.local.*
import com.debbiedoesit.debbieai.jobs.ui.components.*
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    viewModel: JobViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onContactClick: (Long) -> Unit,
    onPhotoClick: (Long) -> Unit,
    onEstimateClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val job by viewModel.selectedJob.collectAsState(initial = null)
    var showStatusMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(jobId) {
        viewModel.selectJob(jobId)
    }
    
    if (job == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    val currentJob = job!!
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Job") },
            text = { Text("Are you sure you want to delete \"${currentJob.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJob(currentJob.id)
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
                title = { 
                    Column {
                        Text(
                            text = currentJob.title,
                            maxLines = 1
                        )
                        if (currentJob.jobNumber.isNotEmpty()) {
                            Text(
                                text = "#${currentJob.jobNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
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
        ) {
            // Status & Priority Header
            Surface(
                color = getStatusColor(currentJob.status).copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Clickable status chip
                        Box {
                            TextButton(onClick = { showStatusMenu = true }) {
                                JobStatusChip(status = currentJob.status)
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            // Status dropdown
                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false }
                            ) {
                                JobStatus.values().forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.displayName) },
                                        onClick = {
                                            viewModel.updateStatus(currentJob.id, status)
                                            showStatusMenu = false
                                        },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .padding(2.dp)
                                            ) {
                                                Surface(
                                                    color = getStatusColor(status),
                                                    shape = MaterialTheme.shapes.extraSmall,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {}
                                            }
                                        },
                                        trailingIcon = if (currentJob.status == status) {
                                            { Icon(Icons.Default.Check, contentDescription = null) }
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Priority",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        JobPriorityIndicator(priority = currentJob.priority)
                    }
                }
            }
            
            // Quick Actions
            if (currentJob.status.isOpen()) {
                QuickActionsRow(
                    job = currentJob,
                    onStartJob = { viewModel.startJob(currentJob.id) },
                    onCompleteJob = { viewModel.completeJob(currentJob.id) },
                    onPutOnHold = { viewModel.putOnHold(currentJob.id) }
                )
            }
            
            Divider()
            
            // Job Type
            ListItem(
                headlineContent = { Text("Job Type") },
                supportingContent = { Text(currentJob.jobType.displayName) },
                leadingContent = {
                    Icon(getJobTypeIcon(currentJob.jobType), contentDescription = null)
                }
            )
            
            // Description
            if (currentJob.description.isNotEmpty()) {
                ListItem(
                    headlineContent = { Text("Description") },
                    supportingContent = { Text(currentJob.description) },
                    leadingContent = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                )
            }
            
            Divider()
            
            // Dates Section
            SectionHeader(title = "Schedule")
            
            currentJob.startDate?.let { date ->
                ListItem(
                    headlineContent = { Text("Scheduled Date") },
                    supportingContent = { 
                        Text(
                            date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) +
                            if (currentJob.scheduledTime.isNotEmpty()) " at ${currentJob.scheduledTime}" else ""
                        )
                    },
                    leadingContent = {
                        Icon(Icons.Default.Event, contentDescription = null)
                    }
                )
            }
            
            currentJob.completedDate?.let { date ->
                ListItem(
                    headlineContent = { Text("Completed") },
                    supportingContent = { 
                        Text(date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
                    },
                    leadingContent = {
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                )
            }
            
            // Hours
            if (currentJob.estimatedHours > 0 || currentJob.actualHours > 0) {
                ListItem(
                    headlineContent = { Text("Hours") },
                    supportingContent = { 
                        Text("Estimated: ${currentJob.estimatedHours}h | Actual: ${currentJob.actualHours}h")
                    },
                    leadingContent = {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    }
                )
            }
            
            Divider()
            
            // Financials Section
            SectionHeader(title = "Financials")
            
            if (currentJob.estimatedCost > 0) {
                ListItem(
                    headlineContent = { Text("Estimated Cost") },
                    supportingContent = { 
                        Text(
                            "$${String.format("%,.2f", currentJob.estimatedCost)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingContent = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    }
                )
            }
            
            if (currentJob.depositAmount > 0) {
                ListItem(
                    headlineContent = { Text("Deposit") },
                    supportingContent = { 
                        Text("$${String.format("%,.2f", currentJob.depositAmount)}")
                    },
                    trailingContent = {
                        if (currentJob.depositPaid) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    "PAID",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.markDepositPaid(currentJob.id) }
                            ) {
                                Text("Mark Paid")
                            }
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Default.Receipt, contentDescription = null)
                    }
                )
            }
            
            // Payment status
            if (currentJob.status == JobStatus.COMPLETED || 
                currentJob.status == JobStatus.INVOICED ||
                currentJob.status == JobStatus.PAID) {
                ListItem(
                    headlineContent = { Text("Final Payment") },
                    trailingContent = {
                        if (currentJob.finalPaid) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    "PAID",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.markFinalPaid(currentJob.id) }
                            ) {
                                Text("Mark Paid")
                            }
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Default.Payments, contentDescription = null)
                    }
                )
            }
            
            Divider()
            
            // Crew Section
            SectionHeader(title = "Crew")
            
            if (currentJob.assignedTo.isEmpty()) {
                ListItem(
                    headlineContent = { Text("No crew assigned") },
                    supportingContent = { Text("Tap to assign team members") },
                    leadingContent = {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                    }
                )
            } else {
                currentJob.assignedTo.forEach { member ->
                    ListItem(
                        headlineContent = { Text(member) },
                        leadingContent = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    )
                }
            }
            
            Divider()
            
            // Linked Items Section
            SectionHeader(title = "Linked Items")
            
            // Customer link
            currentJob.contactId?.let { contactId ->
                ListItem(
                    headlineContent = { Text("Customer") },
                    supportingContent = { Text("Tap to view") },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    },
                    modifier = Modifier.clickable { onContactClick(contactId) }
                )
            } ?: run {
                ListItem(
                    headlineContent = { Text("No customer linked") },
                    supportingContent = { Text("Tap to link a customer") },
                    leadingContent = {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                    }
                )
            }
            
            // Photos link (placeholder count)
            ListItem(
                headlineContent = { Text("Photos") },
                supportingContent = { Text("0 photos") },
                leadingContent = {
                    Icon(Icons.Default.Photo, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            )
            
            // Estimates link
            ListItem(
                headlineContent = { Text("Estimates") },
                supportingContent = { Text("0 estimates") },
                leadingContent = {
                    Icon(Icons.Default.RequestQuote, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            )
            
            // Tasks link
            ListItem(
                headlineContent = { Text("Tasks") },
                supportingContent = { Text("0 tasks") },
                leadingContent = {
                    Icon(Icons.Default.Checklist, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            )
            
            Divider()
            
            // Notes
            if (currentJob.notes.isNotEmpty()) {
                SectionHeader(title = "Notes")
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = currentJob.notes,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Tags
            if (currentJob.tags.isNotEmpty()) {
                SectionHeader(title = "Tags")
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentJob.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(tag) }
                        )
                    }
                }
            }
            
            // Source
            ListItem(
                headlineContent = { Text("Lead Source") },
                supportingContent = { 
                    Text(currentJob.source.displayName + 
                        if (currentJob.referredBy.isNotEmpty()) " (${currentJob.referredBy})" else "")
                },
                leadingContent = {
                    Icon(Icons.Default.Source, contentDescription = null)
                }
            )
            
            // Metadata
            ListItem(
                headlineContent = { Text("Created") },
                supportingContent = { 
                    Text(currentJob.createdAt.format(
                        DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
                    ))
                },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun QuickActionsRow(
    job: Job,
    onStartJob: () -> Unit,
    onCompleteJob: () -> Unit,
    onPutOnHold: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (job.status) {
            JobStatus.LEAD, JobStatus.QUOTED, JobStatus.APPROVED -> {
                Button(
                    onClick = onStartJob,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Job")
                }
            }
            JobStatus.SCHEDULED -> {
                Button(
                    onClick = onStartJob,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start")
                }
                OutlinedButton(
                    onClick = onPutOnHold,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hold")
                }
            }
            JobStatus.IN_PROGRESS -> {
                Button(
                    onClick = onCompleteJob,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Complete")
                }
                OutlinedButton(
                    onClick = onPutOnHold,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hold")
                }
            }
            JobStatus.ON_HOLD -> {
                Button(
                    onClick = onStartJob,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resume")
                }
            }
            else -> {}
        }
    }
}

// Extension to make ListItem clickable
@Composable
private fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun Modifier.clickable(onClick: () -> Unit) = 
    androidx.compose.foundation.clickable(onClick = onClick).let { this.then(it) }
