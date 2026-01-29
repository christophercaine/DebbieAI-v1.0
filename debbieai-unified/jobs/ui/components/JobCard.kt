package com.debbiedoesit.debbieai.jobs.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.jobs.data.local.*
import java.time.format.DateTimeFormatter

/**
 * Job card for grid/list views
 */
@Composable
fun JobCard(
    job: Job,
    contactName: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Status + Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JobStatusChip(status = job.status)
                
                if (job.priority != JobPriority.NORMAL) {
                    JobPriorityIndicator(priority = job.priority)
                }
            }
            
            // Title
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Job number & type
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (job.jobNumber.isNotEmpty()) {
                    Text(
                        text = "#${job.jobNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                JobTypeChip(type = job.jobType, compact = true)
            }
            
            // Customer name
            if (contactName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = contactName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Date info
            job.startDate?.let { date ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (job.scheduledTime.isNotEmpty()) {
                        Text(
                            text = "• ${job.scheduledTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Financials (if quoted/in progress)
            if (job.estimatedCost > 0 && job.status.isOpen()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%,.2f", job.estimatedCost)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (job.depositAmount > 0) {
                        Surface(
                            color = if (job.depositPaid) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (job.depositPaid) "Deposit ✓" else "Deposit pending",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            
            // Crew assigned
            if (job.assignedTo.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = job.assignedTo.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Compact job list item
 */
@Composable
fun JobListItem(
    job: Job,
    contactName: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(getStatusColor(job.status))
            )
            
            // Main content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (contactName != null) {
                        Text(
                            text = contactName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    job.startDate?.let { date ->
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("MMM d")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Priority indicator
            if (job.priority == JobPriority.URGENT || job.priority == JobPriority.HIGH) {
                JobPriorityIndicator(priority = job.priority)
            }
            
            // Amount
            if (job.estimatedCost > 0) {
                Text(
                    text = "$${String.format("%,.0f", job.estimatedCost)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Job status chip
 */
@Composable
fun JobStatusChip(
    status: JobStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = getStatusColor(status).copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = getStatusColor(status),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Job type chip
 */
@Composable
fun JobTypeChip(
    type: JobType,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!compact) {
                Icon(
                    imageVector = getJobTypeIcon(type),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = if (compact) type.displayName.take(8) else type.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Priority indicator
 */
@Composable
fun JobPriorityIndicator(
    priority: JobPriority,
    modifier: Modifier = Modifier
) {
    val color = when (priority) {
        JobPriority.URGENT -> Color(0xFFD32F2F)
        JobPriority.HIGH -> Color(0xFFF57C00)
        JobPriority.NORMAL -> Color(0xFF388E3C)
        JobPriority.LOW -> Color(0xFF757575)
    }
    
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
            Icon(
                imageVector = when (priority) {
                    JobPriority.URGENT -> Icons.Default.PriorityHigh
                    JobPriority.HIGH -> Icons.Default.KeyboardArrowUp
                    else -> Icons.Default.Remove
                },
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            if (priority == JobPriority.URGENT) {
                Text(
                    text = "URGENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ===== HELPER FUNCTIONS =====

@Composable
fun getStatusColor(status: JobStatus): Color {
    return when (status) {
        JobStatus.LEAD -> Color(0xFF9E9E9E)
        JobStatus.QUOTED -> Color(0xFF2196F3)
        JobStatus.APPROVED -> Color(0xFF4CAF50)
        JobStatus.SCHEDULED -> Color(0xFF00BCD4)
        JobStatus.IN_PROGRESS -> Color(0xFFFF9800)
        JobStatus.ON_HOLD -> Color(0xFFFFEB3B)
        JobStatus.COMPLETED -> Color(0xFF8BC34A)
        JobStatus.INVOICED -> Color(0xFF673AB7)
        JobStatus.PAID -> Color(0xFF4CAF50)
        JobStatus.CANCELLED -> Color(0xFFF44336)
    }
}

fun getJobTypeIcon(type: JobType) = when (type) {
    JobType.FLOORING -> Icons.Default.Layers
    JobType.KITCHEN -> Icons.Default.Kitchen
    JobType.BATHROOM -> Icons.Default.Bathtub
    JobType.BASEMENT -> Icons.Default.Foundation
    JobType.ROOFING -> Icons.Default.Roofing
    JobType.PAINTING -> Icons.Default.FormatPaint
    JobType.DECK -> Icons.Default.Deck
    JobType.ELECTRICAL -> Icons.Default.ElectricalServices
    JobType.PLUMBING -> Icons.Default.Plumbing
    JobType.HVAC -> Icons.Default.AcUnit
    JobType.WINDOWS -> Icons.Default.Window
    JobType.LANDSCAPING -> Icons.Default.Grass
    JobType.INSPECTION -> Icons.Default.Search
    else -> Icons.Default.Build
}
