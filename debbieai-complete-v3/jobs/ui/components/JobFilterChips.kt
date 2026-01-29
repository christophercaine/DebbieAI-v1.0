package com.debbiedoesit.debbieai.jobs.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.jobs.data.local.JobStatus
import com.debbiedoesit.debbieai.jobs.data.local.JobType
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewMode

/**
 * Status filter chips - horizontal scrollable row
 */
@Composable
fun JobStatusFilterChips(
    selectedStatus: JobStatus?,
    onStatusSelected: (JobStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        
        // All filter
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedStatus == null) {
                { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null
        )
        
        // Open jobs
        FilterChip(
            selected = false,
            onClick = { /* Special filter for open */ },
            label = { Text("Open") },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        )
        
        // Common statuses
        val commonStatuses = listOf(
            JobStatus.LEAD,
            JobStatus.QUOTED,
            JobStatus.SCHEDULED,
            JobStatus.IN_PROGRESS,
            JobStatus.COMPLETED
        )
        
        commonStatuses.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { 
                    onStatusSelected(if (selectedStatus == status) null else status) 
                },
                label = { Text(status.displayName) },
                leadingIcon = if (selectedStatus == status) {
                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getStatusColor(status).copy(alpha = 0.2f)
                )
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
    }
}

/**
 * Job type filter dropdown
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobTypeFilter(
    selectedType: JobType?,
    onTypeSelected: (JobType?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedType?.displayName ?: "All Types",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(160.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Types") },
                onClick = {
                    onTypeSelected(null)
                    expanded = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Category, contentDescription = null)
                }
            )
            
            Divider()
            
            JobType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(getJobTypeIcon(type), contentDescription = null)
                    },
                    trailingIcon = if (selectedType == type) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}

/**
 * View mode toggle (List / Kanban / Calendar)
 */
@Composable
fun JobViewModeToggle(
    currentMode: JobViewMode,
    onModeChanged: (JobViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { onModeChanged(JobViewMode.LIST) }
        ) {
            Icon(
                imageVector = Icons.Default.ViewList,
                contentDescription = "List view",
                tint = if (currentMode == JobViewMode.LIST) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = { onModeChanged(JobViewMode.KANBAN) }
        ) {
            Icon(
                imageVector = Icons.Default.ViewKanban,
                contentDescription = "Kanban view",
                tint = if (currentMode == JobViewMode.KANBAN) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = { onModeChanged(JobViewMode.CALENDAR) }
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Calendar view",
                tint = if (currentMode == JobViewMode.CALENDAR) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Quick stats bar showing job counts
 */
@Composable
fun JobStatsBar(
    totalJobs: Int,
    activeJobs: Int,
    completedThisMonth: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            value = totalJobs.toString(),
            label = "Total",
            icon = Icons.Default.Work
        )
        
        StatItem(
            value = activeJobs.toString(),
            label = "Active",
            icon = Icons.Default.PlayArrow,
            highlight = activeJobs > 0
        )
        
        StatItem(
            value = completedThisMonth.toString(),
            label = "This Month",
            icon = Icons.Default.CheckCircle
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (highlight) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = if (highlight) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sort options for jobs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSortMenu(
    currentSort: JobSortOption,
    onSortChanged: (JobSortOption) -> Unit,
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
            JobSortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        onSortChanged(option)
                        expanded = false
                    },
                    trailingIcon = if (currentSort == option) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}

enum class JobSortOption(val displayName: String) {
    DATE_ASC("Date (Oldest)"),
    DATE_DESC("Date (Newest)"),
    PRIORITY("Priority"),
    STATUS("Status"),
    AMOUNT_HIGH("Amount (High)"),
    AMOUNT_LOW("Amount (Low)"),
    NAME_AZ("Name (A-Z)"),
    NAME_ZA("Name (Z-A)")
}
