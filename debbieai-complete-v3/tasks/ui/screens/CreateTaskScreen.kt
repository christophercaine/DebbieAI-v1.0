package com.debbiedoesit.debbieai.tasks.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.tasks.data.local.*
import com.debbiedoesit.debbieai.tasks.ui.components.*
import com.debbiedoesit.debbieai.tasks.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel,
    onBackClick: () -> Unit,
    contactId: Long? = null,
    jobId: Long? = null,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.GENERAL) }
    var priority by remember { mutableStateOf(TaskPriority.NORMAL) }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var dueTime by remember { mutableStateOf<LocalTime?>(null) }
    var location by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var estimatedMinutes by remember { mutableStateOf("") }
    var setReminder by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf(30) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dueDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Task") },
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
                                viewModel.createTask(
                                    title = title,
                                    description = description,
                                    category = category,
                                    priority = priority,
                                    dueDate = dueDate,
                                    dueTime = dueTime,
                                    contactId = contactId,
                                    jobId = jobId,
                                    reminderMinutesBefore = if (setReminder) reminderMinutes else null
                                )
                                onBackClick()
                            }
                        },
                        enabled = title.isNotBlank() && !isLoading
                    ) {
                        Text("Save")
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
            // Quick templates
            QuickTaskTemplates(
                onTemplateSelected = { template ->
                    title = template.title
                    category = template.category
                    estimatedMinutes = template.estimatedMinutes.toString()
                }
            )
            
            Divider()
            
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Task, contentDescription = null) }
            )
            
            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    leadingIcon = { Icon(getCategoryIcon(category), contentDescription = null) }
                )
                
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    TaskCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            },
                            leadingIcon = { Icon(getCategoryIcon(cat), contentDescription = null) }
                        )
                    }
                }
            }
            
            // Priority
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getPriorityColor(p).copy(alpha = 0.2f)
                        )
                    )
                }
            }
            
            Divider()
            
            // Due Date
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date button
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        dueDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) 
                            ?: "Set Date"
                    )
                }
                
                // Time button
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    enabled = dueDate != null
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        dueTime?.format(DateTimeFormatter.ofPattern("h:mm a")) 
                            ?: "Set Time"
                    )
                }
            }
            
            // Quick date buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { dueDate = LocalDate.now() },
                    label = { Text("Today") }
                )
                AssistChip(
                    onClick = { dueDate = LocalDate.now().plusDays(1) },
                    label = { Text("Tomorrow") }
                )
                AssistChip(
                    onClick = { dueDate = LocalDate.now().plusWeeks(1) },
                    label = { Text("Next Week") }
                )
                AssistChip(
                    onClick = { dueDate = null; dueTime = null },
                    label = { Text("No Date") }
                )
            }
            
            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Text("Set Reminder")
                }
                Switch(
                    checked = setReminder,
                    onCheckedChange = { setReminder = it },
                    enabled = dueDate != null
                )
            }
            
            if (setReminder && dueDate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 60, 120).forEach { mins ->
                        FilterChip(
                            selected = reminderMinutes == mins,
                            onClick = { reminderMinutes = mins },
                            label = { 
                                Text(
                                    if (mins < 60) "$mins min" else "${mins/60} hr"
                                ) 
                            }
                        )
                    }
                }
            }
            
            Divider()
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )
            
            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )
            
            // Assigned to
            OutlinedTextField(
                value = assignedTo,
                onValueChange = { assignedTo = it },
                label = { Text("Assign To") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            
            // Estimated time
            OutlinedTextField(
                value = estimatedMinutes,
                onValueChange = { estimatedMinutes = it.filter { c -> c.isDigit() } },
                label = { Text("Estimated Time (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) }
            )
            
            // Linked items info
            if (contactId != null || jobId != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Linked To",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        if (contactId != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Contact #$contactId")
                            }
                        }
                        
                        if (jobId != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Job #$jobId")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        isLoading = true
                        viewModel.createTask(
                            title = title,
                            description = description,
                            category = category,
                            priority = priority,
                            dueDate = dueDate,
                            dueTime = dueTime,
                            contactId = contactId,
                            jobId = jobId,
                            reminderMinutesBefore = if (setReminder) reminderMinutes else null
                        )
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = title.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Task")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,
    viewModel: TaskViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var category by remember { mutableStateOf(task.category) }
    var priority by remember { mutableStateOf(task.priority) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var dueTime by remember { mutableStateOf(task.dueTime) }
    var location by remember { mutableStateOf(task.location) }
    var assignedTo by remember { mutableStateOf(task.assignedTo) }
    var estimatedMinutes by remember { mutableStateOf(task.estimatedMinutes.toString()) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = task.dueDate?.toEpochDay()?.times(24 * 60 * 60 * 1000)
    )
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dueDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                category = category,
                                priority = priority,
                                dueDate = dueDate,
                                dueTime = dueTime,
                                location = location,
                                assignedTo = assignedTo,
                                estimatedMinutes = estimatedMinutes.toIntOrNull() ?: 0
                            )
                            viewModel.updateTask(updatedTask)
                            onBackClick()
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save")
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
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    TaskCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            },
                            leadingIcon = { Icon(getCategoryIcon(cat), contentDescription = null) }
                        )
                    }
                }
            }
            
            // Priority
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.displayName) }
                    )
                }
            }
            
            // Due Date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    dueDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) 
                        ?: "Set Due Date"
                )
            }
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )
            
            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Assigned to
            OutlinedTextField(
                value = assignedTo,
                onValueChange = { assignedTo = it },
                label = { Text("Assign To") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Estimated time
            OutlinedTextField(
                value = estimatedMinutes,
                onValueChange = { estimatedMinutes = it.filter { c -> c.isDigit() } },
                label = { Text("Estimated Time (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = {
                    val updatedTask = task.copy(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        location = location,
                        assignedTo = assignedTo,
                        estimatedMinutes = estimatedMinutes.toIntOrNull() ?: 0
                    )
                    viewModel.updateTask(updatedTask)
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = title.isNotBlank()
            ) {
                Text("Save Changes")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
