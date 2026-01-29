package com.debbiedoesit.debbieai.estimates.ui.screens

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
import com.debbiedoesit.debbieai.estimates.data.local.Estimate
import com.debbiedoesit.debbieai.estimates.data.local.EstimateStatus
import com.debbiedoesit.debbieai.estimates.ui.components.*
import com.debbiedoesit.debbieai.estimates.viewmodel.EstimateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimateListScreen(viewModel: EstimateViewModel, onEstimateClick: (Long) -> Unit, onCreateClick: () -> Unit) {
    val estimates by viewModel.filteredEstimates.collectAsState(initial = emptyList())
    val summary by viewModel.summary.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.message) { uiState.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessage() } }
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Estimates") }, actions = { IconButton(onClick = { viewModel.loadSummary() }) { Icon(Icons.Default.Refresh, "Refresh") } }) },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = onCreateClick, icon = { Icon(Icons.Default.Add, null) }, text = { Text("New Estimate") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            EstimateStatsBar(summary, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            EstimateStatusFilterChips(statusFilter, { viewModel.setStatusFilter(it) }, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            
            if (estimates.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, null, Modifier.size(64.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Text("No estimates yet", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onCreateClick) { Text("Create First Estimate") }
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(estimates, key = { it.id }) { estimate ->
                        EstimateCard(estimate = estimate, onClick = { onEstimateClick(estimate.id) })
                    }
                }
            }
        }
    }
}