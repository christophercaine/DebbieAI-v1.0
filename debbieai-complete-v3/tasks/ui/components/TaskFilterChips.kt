package com.debbiedoesit.debbieai.tasks.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.tasks.data.local.*
import com.debbiedoesit.debbieai.tasks.viewmodel.TaskViewMode

/**
 * Horizontal scrollable status filter chips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatusFilterChips(
    selectedStatus: TaskStatus?,
    onStatusSelected: (TaskStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All tasks chip
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedStatus == null) {
                { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
            } else null
        )
        
        // Status chips
        TaskStatus.values().forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(if (selectedStatus == status) null else status) },
                label = { Text(status.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = getStatusIcon(status),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

/**
 * Category filter dropdown
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCategoryFilter(
    selectedCategory: TaskCategory?,
    onCategorySelected: (TaskCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory?.displayName ?: "All Categories",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().width(180.dp),
            textStyle = MaterialTheme.typography.bodySmall
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Categories") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null) }
            )
            
            Divider()
            
            TaskCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(getCategoryIcon(category), contentDescription = null)
                    }
                )
            }
        }
    }
}

/**
 * Priority filter chips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPriorityFilterChips(
    selectedPriority: TaskPriority?,
    onPrioritySelected: (TaskPriority?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPriority == null,
            onClick = { onPrioritySelected(null) },
            label = { Text("Any Priority") }
        )
        
        TaskPriority.values().forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(if (selectedPriority == priority) null else priority) },
                label = { Text(priority.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getPriorityColor(priority).copy(alpha = 0.2f)
                )
            )
        }
    }
}

/**
 * View mode toggle (List, Today, Calendar, Kanban)
 */
@Composable
fun TaskViewModeToggle(
    currentMode: TaskViewMode,
    onModeSelected: (TaskViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TaskViewMode.values().forEach { mode ->
            IconButton(
                onClick = { onModeSelected(mode) }
            ) {
                Icon(
                    imageVector = getViewModeIcon(mode),
                    contentDescription = mode.name,
                    tint = if (currentMode == mode) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Quick stats bar for tasks
 */
@Composable
fun TaskStatsBar(
    summary: TaskSummary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            value = summary.pendingTasks.toString(),
            label = "To Do",
            color = MaterialTheme.colorScheme.primary
        )
        StatItem(
            value = summary.overdueTasks.toString(),
            label = "Overdue",
            color = MaterialTheme.colorScheme.error
        )
        StatItem(
            value = summary.dueTodayTasks.toString(),
            label = "Today",
            color = MaterialTheme.colorScheme.tertiary
        )
        StatItem(
            value = summary.completedToday.toString(),
            label = "Done",
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Quick task templates row
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTaskTemplates(
    onTemplateSelected: (QuickTaskTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Add",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickTaskTemplate.values().take(6).forEach { template ->
                AssistChip(
                    onClick = { onTemplateSelected(template) },
                    label = { Text(template.title, maxLines = 1) },
                    leadingIcon = {
                        Icon(
                            getCategoryIcon(template.category),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Sort menu for tasks
 */
@Composable
fun TaskSortMenu(
    currentSort: TaskSortOption,
    onSortSelected: (TaskSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Sort, contentDescription = "Sort")
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskSortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    },
                    leadingIcon = if (currentSort == option) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}

enum class TaskSortOption(val displayName: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    STATUS("Status"),
    CREATED("Created"),
    CATEGORY("Category"),
    ALPHABETICAL("A-Z")
}

// ===== HELPER FUNCTIONS =====

fun getStatusIcon(status: TaskStatus): ImageVector = when (status) {
    TaskStatus.PENDING -> Icons.Default.RadioButtonUnchecked
    TaskStatus.IN_PROGRESS -> Icons.Default.PlayCircle
    TaskStatus.WAITING -> Icons.Default.Pause
    TaskStatus.COMPLETED -> Icons.Default.CheckCircle
    TaskStatus.CANCELLED -> Icons.Default.Cancel
}

@Composable
fun getStatusColor(status: TaskStatus) = when (status) {
    TaskStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
    TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    TaskStatus.WAITING -> MaterialTheme.colorScheme.tertiary
    TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    TaskStatus.CANCELLED -> MaterialTheme.colorScheme.error
}

fun getCategoryIcon(category: TaskCategory): ImageVector = when (category) {
    TaskCategory.GENERAL -> Icons.Default.Task
    TaskCategory.CALL -> Icons.Default.Phone
    TaskCategory.EMAIL -> Icons.Default.Email
    TaskCategory.MEETING -> Icons.Default.People
    TaskCategory.SITE_VISIT -> Icons.Default.LocationOn
    TaskCategory.ESTIMATE -> Icons.Default.Calculate
    TaskCategory.FOLLOW_UP -> Icons.Default.Reply
    TaskCategory.PURCHASE -> Icons.Default.ShoppingCart
    TaskCategory.INSPECTION -> Icons.Default.Search
    TaskCategory.SCHEDULING -> Icons.Default.CalendarMonth
    TaskCategory.PAPERWORK -> Icons.Default.Description
    TaskCategory.PAYMENT -> Icons.Default.Payment
    TaskCategory.WARRANTY -> Icons.Default.Shield
    TaskCategory.OTHER -> Icons.Default.MoreHoriz
}

@Composable
fun getPriorityColor(priority: TaskPriority) = when (priority) {
    TaskPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
    TaskPriority.NORMAL -> MaterialTheme.colorScheme.primary
    TaskPriority.HIGH -> MaterialTheme.colorScheme.tertiary
    TaskPriority.URGENT -> MaterialTheme.colorScheme.error
}

fun getViewModeIcon(mode: TaskViewMode): ImageVector = when (mode) {
    TaskViewMode.LIST -> Icons.Default.List
    TaskViewMode.TODAY -> Icons.Default.Today
    TaskViewMode.CALENDAR -> Icons.Default.CalendarViewMonth
    TaskViewMode.KANBAN -> Icons.Default.ViewKanban
}
