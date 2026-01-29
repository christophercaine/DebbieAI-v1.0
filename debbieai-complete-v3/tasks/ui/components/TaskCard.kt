package com.debbiedoesit.debbieai.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.tasks.data.local.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Task card for list views
 */
@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteClick: () -> Unit,
    contactName: String? = null,
    jobTitle: String? = null,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isOverdue) 4.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                task.isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { if (!isCompleted) onCompleteClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = getPriorityColor(task.priority)
                )
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title with strikethrough if completed
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (!isCompleted) FontWeight.Medium else FontWeight.Normal,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else 
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Category & Priority row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskCategoryChip(category = task.category, compact = true)
                    
                    if (task.priority != TaskPriority.NORMAL) {
                        TaskPriorityIndicator(priority = task.priority)
                    }
                }
                
                // Due date
                task.dueDate?.let { date ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isOverdue) Icons.Default.Warning else Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (task.isOverdue) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDueDate(date, task.dueTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isOverdue) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Linked contact/job
                if (contactName != null || jobTitle != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (contactName != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = contactName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (jobTitle != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = jobTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            
            // Reminder indicator
            if (task.reminderEnabled) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Reminder set",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Compact task list item for dense views
 */
@Composable
fun TaskListItem(
    task: Task,
    onTaskClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        tonalElevation = if (task.isOverdue) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(getPriorityColor(task.priority))
            )
            
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { if (!isCompleted) onCompleteClick() },
                modifier = Modifier.size(24.dp)
            )
            
            // Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                color = if (isCompleted) 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else 
                    MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            // Due date badge
            task.dueDate?.let { date ->
                DueDateBadge(date = date, isOverdue = task.isOverdue)
            }
            
            // Category icon
            Icon(
                imageVector = getCategoryIcon(task.category),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Task category chip
 */
@Composable
fun TaskCategoryChip(
    category: TaskCategory,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = getCategoryColor(category).copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = getCategoryColor(category)
            )
            if (!compact) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = getCategoryColor(category)
                )
            }
        }
    }
}

/**
 * Task priority indicator
 */
@Composable
fun TaskPriorityIndicator(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val color = getPriorityColor(priority)
    
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (priority) {
                TaskPriority.URGENT -> {
                    Icon(
                        Icons.Default.PriorityHigh,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = color
                    )
                    Text(
                        text = "!",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                TaskPriority.HIGH -> {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = color
                    )
                }
                else -> {}
            }
        }
    }
}

/**
 * Due date badge
 */
@Composable
fun DueDateBadge(
    date: LocalDate,
    isOverdue: Boolean,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val color = when {
        isOverdue -> MaterialTheme.colorScheme.error
        date == today -> MaterialTheme.colorScheme.primary
        date == today.plusDays(1) -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when {
                isOverdue -> "Overdue"
                date == today -> "Today"
                date == today.plusDays(1) -> "Tomorrow"
                else -> date.format(DateTimeFormatter.ofPattern("MMM d"))
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// ===== HELPER FUNCTIONS =====

@Composable
fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.URGENT -> Color(0xFFD32F2F)
        TaskPriority.HIGH -> Color(0xFFF57C00)
        TaskPriority.NORMAL -> Color(0xFF388E3C)
        TaskPriority.LOW -> Color(0xFF757575)
    }
}

@Composable
fun getCategoryColor(category: TaskCategory): Color {
    return when (category) {
        TaskCategory.CALL -> Color(0xFF4CAF50)
        TaskCategory.EMAIL -> Color(0xFF2196F3)
        TaskCategory.MEETING -> Color(0xFF9C27B0)
        TaskCategory.SITE_VISIT -> Color(0xFFFF5722)
        TaskCategory.ESTIMATE -> Color(0xFF00BCD4)
        TaskCategory.FOLLOW_UP -> Color(0xFFFF9800)
        TaskCategory.PURCHASE -> Color(0xFF795548)
        TaskCategory.INSPECTION -> Color(0xFF607D8B)
        TaskCategory.SCHEDULING -> Color(0xFF3F51B5)
        TaskCategory.PAPERWORK -> Color(0xFF9E9E9E)
        TaskCategory.PAYMENT -> Color(0xFF4CAF50)
        TaskCategory.WARRANTY -> Color(0xFFE91E63)
        else -> Color(0xFF757575)
    }
}

fun getCategoryIcon(category: TaskCategory): ImageVector {
    return when (category) {
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
        else -> Icons.Default.Task
    }
}

fun formatDueDate(date: LocalDate, time: java.time.LocalTime?): String {
    val today = LocalDate.now()
    val dateStr = when {
        date == today -> "Today"
        date == today.plusDays(1) -> "Tomorrow"
        date == today.minusDays(1) -> "Yesterday"
        date.year == today.year -> date.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        else -> date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
    
    return if (time != null) {
        "$dateStr at ${time.format(DateTimeFormatter.ofPattern("h:mm a"))}"
    } else {
        dateStr
    }
}
