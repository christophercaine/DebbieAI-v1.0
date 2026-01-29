package com.debbiedoesit.debbieai.jobs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.jobs.data.local.Job
import com.debbiedoesit.debbieai.jobs.data.local.JobStatus
import com.debbiedoesit.debbieai.jobs.ui.components.*
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewModel
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(
    viewModel: JobViewModel,
    onJobClick: (Long) -> Unit,
    onCreateJob: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredJobs by viewModel.filteredJobs.collectAsState(initial = emptyList())
    val summary by viewModel.summary.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jobs") },
                actions = {
                    JobViewModeToggle(
                        currentMode = viewMode,
                        onModeChanged = { viewModel.setViewMode(it) }
                    )
                    
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateJob,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Job") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats bar
            JobStatsBar(
                totalJobs = summary.totalJobs,
                activeJobs = summary.activeJobs,
                completedThisMonth = summary.completedThisMonth
            )
            
            Divider()
            
            // Filters row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type filter dropdown
                JobTypeFilter(
                    selectedType = typeFilter,
                    onTypeSelected = { viewModel.setTypeFilter(it) },
                    modifier = Modifier.weight(1f)
                )
                
                // Clear filters button
                if (statusFilter != null || typeFilter != null) {
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear")
                    }
                }
            }
            
            // Status filter chips
            JobStatusFilterChips(
                selectedStatus = statusFilter,
                onStatusSelected = { viewModel.setStatusFilter(it) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content based on view mode
            when (viewMode) {
                JobViewMode.LIST -> {
                    JobListView(
                        jobs = filteredJobs,
                        onJobClick = onJobClick
                    )
                }
                JobViewMode.KANBAN -> {
                    JobKanbanView(
                        viewModel = viewModel,
                        onJobClick = onJobClick
                    )
                }
                JobViewMode.CALENDAR -> {
                    JobCalendarView(
                        viewModel = viewModel,
                        onJobClick = onJobClick
                    )
                }
                JobViewMode.MAP -> {
                    // Future: Map view
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Map view coming soon!")
                    }
                }
            }
        }
    }
}

/**
 * Standard list view
 */
@Composable
private fun JobListView(
    jobs: List<Job>,
    onJobClick: (Long) -> Unit
) {
    if (jobs.isEmpty()) {
        EmptyJobsState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = jobs,
                key = { it.id }
            ) { job ->
                JobCard(
                    job = job,
                    onClick = { onJobClick(job.id) }
                )
            }
            
            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * Kanban board view - columns by status
 */
@Composable
private fun JobKanbanView(
    viewModel: JobViewModel,
    onJobClick: (Long) -> Unit
) {
    val jobsByStatus by viewModel.jobsByStatus.collectAsState(initial = emptyMap())
    
    val visibleStatuses = listOf(
        JobStatus.LEAD,
        JobStatus.QUOTED,
        JobStatus.SCHEDULED,
        JobStatus.IN_PROGRESS,
        JobStatus.COMPLETED
    )
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        visibleStatuses.forEach { status ->
            val statusJobs = jobsByStatus[status] ?: emptyList()
            
            KanbanColumn(
                status = status,
                jobs = statusJobs,
                onJobClick = onJobClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KanbanColumn(
    status: JobStatus,
    jobs: List<Job>,
    onJobClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Column header
            Surface(
                color = getStatusColor(status).copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = getStatusColor(status)
                    )
                    Badge {
                        Text(jobs.size.toString())
                    }
                }
            }
            
            // Jobs in column
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(jobs, key = { it.id }) { job ->
                    KanbanJobCard(
                        job = job,
                        onClick = { onJobClick(job.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanJobCard(
    job: Job,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            
            if (job.estimatedCost > 0) {
                Text(
                    text = "$${String.format("%,.0f", job.estimatedCost)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Calendar view placeholder
 */
@Composable
private fun JobCalendarView(
    viewModel: JobViewModel,
    onJobClick: (Long) -> Unit
) {
    // Simple placeholder - would implement full calendar
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Calendar View",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Full calendar coming soon",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state
 */
@Composable
private fun EmptyJobsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            
            Text(
                text = "No jobs yet",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = "Create your first job to get started\ntracking your work",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
