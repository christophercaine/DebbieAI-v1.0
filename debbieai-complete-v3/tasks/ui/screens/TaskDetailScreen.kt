package com.debbiedoesit.debbieai.tasks.ui.screens

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.tasks.data.local.*
import com.debbiedoesit.debbieai.tasks.ui.components.*
import com.debbiedoesit.debbieai.tasks.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    task: Task,
    viewModel: TaskViewModel,
    onBackClick: () -> Unit,
    onContactClick: ((Long) -> Unit)? = null,
    onJobClick: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var completionNotes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task.id)
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
    
    // Completion dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            title = { Text("Complete Task") },
            text = {
                Column {
                    Text("Add completion notes (optional):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = completionNotes,
                        onValueChange = { completionNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Notes...") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeTask(task.id, completionNotes)
                        showCompletionDialog = false
                        onBackClick()
                    }
                ) {
                    Text("Complete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status & Title
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskStatusChip(status = task.status)
                        TaskPriorityIndicator(priority = task.priority)
                    }
                    
                    // Title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                    )
                    
                    // Category
                    TaskCategoryChip(category = task.category)
                    
                    // Description
                    if (task.description.isNotEmpty()) {
                        Divider()
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Quick Actions
            if (!isCompleted) {
                QuickActions(
                    task = task,
                    onComplete = { showCompletionDialog = true },
                    onStartProgress = { viewModel.updateStatus(task.id, TaskStatus.IN_PROGRESS) },
                    onSetWaiting = { viewModel.updateStatus(task.id, TaskStatus.WAITING) }
                )
            } else {
                // Reopen button for completed tasks
                OutlinedButton(
                    onClick = { viewModel.reopenTask(task.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reopen Task")
                }
            }
            
            // Schedule Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Due date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (task.isOverdue) Icons.Default.Warning else Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (task.isOverdue) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column {
                                Text(
                                    text = "Due Date",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = task.dueDate?.let { formatDueDate(it, task.dueTime) } ?: "Not set",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (task.isOverdue) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        if (!isCompleted) {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text(if (task.dueDate == null) "Set" else "Change")
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (task.reminderEnabled) Icons.Default.NotificationsActive 
                                else Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = if (task.reminderEnabled) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column {
                                Text(
                                    text = "Reminder",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = if (task.reminderEnabled && task.reminderDateTime != null) {
                                        task.reminderDateTime.format(
                                            DateTimeFormatter.ofPattern("EEE, MMM d 'at' h:mm a")
                                        )
                                    } else "Not set",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        if (!isCompleted) {
                            TextButton(onClick = { showReminderDialog = true }) {
                                Text(if (task.reminderEnabled) "Change" else "Set")
                            }
                        }
                    }
                }
            }
            
            // Linked Items
            if (task.contactId != null || task.jobId != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Linked To",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        task.contactId?.let { contactId ->
                            Surface(
                                onClick = { onContactClick?.invoke(contactId) },
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                    Text("Contact #$contactId")
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        task.jobId?.let { jobId ->
                            Surface(
                                onClick = { onJobClick?.invoke(jobId) },
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Work, contentDescription = null)
                                    Text("Job #$jobId")
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Assignment & Location
            if (task.assignedTo.isNotEmpty() || task.location.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (task.assignedTo.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Column {
                                    Text(
                                        text = "Assigned To",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = task.assignedTo,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        if (task.location.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Column {
                                    Text(
                                        text = "Location",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = task.location,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Time Tracking
            if (task.estimatedMinutes > 0 || task.actualMinutes > 0) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Time",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formatMinutes(task.estimatedMinutes),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Estimated",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = formatMinutes(task.actualMinutes),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Actual",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
            
            // Completion info (if completed)
            if (isCompleted && task.completedAt != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        Text(
                            text = task.completedAt.format(
                                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        if (task.completionNotes.isNotEmpty()) {
                            Divider()
                            Text(
                                text = task.completionNotes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Metadata
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Created: ${task.createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Updated: ${task.updatedAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickActions(
    task: Task,
    onComplete: () -> Unit,
    onStartProgress: () -> Unit,
    onSetWaiting: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (task.status) {
            TaskStatus.PENDING -> {
                Button(
                    onClick = onStartProgress,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Start")
                }
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Complete")
                }
            }
            TaskStatus.IN_PROGRESS -> {
                OutlinedButton(
                    onClick = onSetWaiting,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Waiting")
                }
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Complete")
                }
            }
            TaskStatus.WAITING -> {
                Button(
                    onClick = onStartProgress,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Resume")
                }
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Complete")
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun TaskStatusChip(status: TaskStatus) {
    Surface(
        color = getStatusColor(status).copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                getStatusIcon(status),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = getStatusColor(status)
            )
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = getStatusColor(status)
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    return if (minutes >= 60) {
        "${minutes / 60}h ${minutes % 60}m"
    } else {
        "${minutes}m"
    }
}
