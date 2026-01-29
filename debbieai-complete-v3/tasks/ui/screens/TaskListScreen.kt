package com.debbiedoesit.debbieai.tasks.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.debbiedoesit.debbieai.tasks.viewmodel.TaskViewMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    
    val filteredTasks by viewModel.filteredTasks.collectAsState(initial = emptyList())
    val todayTasks by viewModel.todayTasks.collectAsState(initial = emptyList())
    val overdueTasks by viewModel.overdueTasks.collectAsState(initial = emptyList())
    val activeTasks by viewModel.activeTasks.collectAsState(initial = emptyList())
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    TaskViewModeToggle(
                        currentMode = viewMode,
                        onModeSelected = { viewModel.setViewMode(it) }
                    )
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats bar
            TaskStatsBar(summary = summary)
            
            Divider()
            
            // Filters
            Column(modifier = Modifier.padding(8.dp)) {
                TaskStatusFilterChips(
                    selectedStatus = statusFilter,
                    onStatusSelected = { viewModel.setStatusFilter(it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskCategoryFilter(
                        selectedCategory = categoryFilter,
                        onCategorySelected = { viewModel.setCategoryFilter(it) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (statusFilter != null || categoryFilter != null) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Clear")
                        }
                    }
                }
            }
            
            Divider()
            
            // Content based on view mode
            when (viewMode) {
                TaskViewMode.LIST -> TaskListView(
                    tasks = filteredTasks,
                    onTaskClick = onTaskClick,
                    onCompleteClick = { viewModel.completeTask(it.id) }
                )
                TaskViewMode.TODAY -> TodayView(
                    todayTasks = todayTasks,
                    overdueTasks = overdueTasks,
                    onTaskClick = onTaskClick,
                    onCompleteClick = { viewModel.completeTask(it.id) }
                )
                TaskViewMode.KANBAN -> TaskKanbanView(
                    tasks = activeTasks,
                    onTaskClick = onTaskClick,
                    onCompleteClick = { viewModel.completeTask(it.id) },
                    onStatusChange = { task, status -> viewModel.updateStatus(task.id, status) }
                )
                TaskViewMode.CALENDAR -> TaskCalendarView(
                    viewModel = viewModel,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}

@Composable
private fun TaskListView(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCompleteClick: (Task) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.TaskAlt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No tasks found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Add a task to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task) },
                    onCompleteClick = { onCompleteClick(task) }
                )
            }
        }
    }
}

@Composable
private fun TodayView(
    todayTasks: List<Task>,
    overdueTasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCompleteClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Overdue section
        if (overdueTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Overdue (${overdueTasks.size})",
                    icon = Icons.Default.Warning,
                    color = MaterialTheme.colorScheme.error
                )
            }
            items(overdueTasks, key = { "overdue_${it.id}" }) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task) },
                    onCompleteClick = { onCompleteClick(task) }
                )
            }
        }
        
        // Today section
        item {
            SectionHeader(
                title = "Today (${todayTasks.size})",
                icon = Icons.Default.Today,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (todayTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "All caught up!",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "No tasks due today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(todayTasks, key = { "today_${it.id}" }) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task) },
                    onCompleteClick = { onCompleteClick(task) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
    }
}

@Composable
private fun TaskKanbanView(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCompleteClick: (Task) -> Unit,
    onStatusChange: (Task, TaskStatus) -> Unit
) {
    val columns = listOf(
        TaskStatus.PENDING to "To Do",
        TaskStatus.IN_PROGRESS to "In Progress",
        TaskStatus.WAITING to "Waiting",
        TaskStatus.COMPLETED to "Done"
    )
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        columns.forEach { (status, title) ->
            val columnTasks = tasks.filter { it.status == status }
            
            KanbanColumn(
                title = title,
                count = columnTasks.size,
                status = status,
                tasks = columnTasks,
                onTaskClick = onTaskClick,
                onCompleteClick = onCompleteClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KanbanColumn(
    title: String,
    count: Int,
    status: TaskStatus,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCompleteClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Badge { Text(count.toString()) }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tasks
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { "kanban_${it.id}" }) { task ->
                    TaskListItem(
                        task = task,
                        onTaskClick = { onTaskClick(task) },
                        onCompleteClick = { onCompleteClick(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCalendarView(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasksForDate by viewModel.tasksForSelectedDate.collectAsState(initial = emptyList())
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Simple date selector (week view)
        WeekDateSelector(
            selectedDate = selectedDate,
            onDateSelected = { viewModel.selectDate(it) }
        )
        
        Divider()
        
        // Tasks for selected date
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            if (tasksForDate.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No tasks for this day",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tasksForDate, key = { "cal_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = { onTaskClick(task) },
                        onCompleteClick = { viewModel.completeTask(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { date ->
            val isSelected = date == selectedDate
            val isToday = date == today
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(
                    text = date.dayOfWeek.name.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Surface(
                    onClick = { onDateSelected(date) },
                    shape = MaterialTheme.shapes.small,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isToday -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}
