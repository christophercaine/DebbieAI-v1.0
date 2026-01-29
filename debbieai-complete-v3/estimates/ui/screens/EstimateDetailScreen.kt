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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.estimates.data.local.*
import com.debbiedoesit.debbieai.estimates.ui.components.*
import com.debbiedoesit.debbieai.estimates.viewmodel.EstimateViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimateDetailScreen(estimateId: Long, viewModel: EstimateViewModel, onBackClick: () -> Unit, onEditClick: (Long) -> Unit, onAddLineItem: (Long) -> Unit) {
    LaunchedEffect(estimateId) { viewModel.selectEstimate(estimateId) }
    val estimate by viewModel.selectedEstimate.collectAsState(initial = null)
    val lineItems by viewModel.selectedEstimateItems.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    
    estimate?.let { est ->
        if (showDeleteDialog) {
            AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete Estimate?") }, text = { Text("This will permanently delete #${est.estimateNumber}") }, confirmButton = { TextButton(onClick = { viewModel.deleteEstimate(est.id); showDeleteDialog = false; onBackClick() }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Column { Text("#${est.estimateNumber}"); Text(est.title, style = MaterialTheme.typography.bodySmall) } },
                    navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                    actions = {
                        IconButton(onClick = { viewModel.duplicateEstimate(est.id) }) { Icon(Icons.Default.ContentCopy, "Duplicate") }
                        IconButton(onClick = { onEditClick(est.id) }) { Icon(Icons.Default.Edit, "Edit") }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                )
            }
        ) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                EstimateStatusChip(est.status)
                                Box {
                                    TextButton(onClick = { showStatusMenu = true }) { Text("Change Status"); Icon(Icons.Default.ArrowDropDown, null) }
                                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                                        if (est.status == EstimateStatus.DRAFT) DropdownMenuItem(text = { Text("Mark as Sent") }, onClick = { viewModel.sendEstimate(est.id); showStatusMenu = false })
                                        if (est.status == EstimateStatus.SENT || est.status == EstimateStatus.VIEWED) {
                                            DropdownMenuItem(text = { Text("Approve") }, onClick = { viewModel.approveEstimate(est.id); showStatusMenu = false })
                                            DropdownMenuItem(text = { Text("Reject") }, onClick = { viewModel.rejectEstimate(est.id); showStatusMenu = false })
                                        }
                                    }
                                }
                            }
                            if (est.description.isNotEmpty()) Text(est.description, style = MaterialTheme.typography.bodyMedium)
                            est.validUntil?.let { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) { Icon(Icons.Default.Schedule, null, Modifier.size(16.dp)); Text("Valid until ${it.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}", style = MaterialTheme.typography.bodySmall) } }
                        }
                    }
                }
                
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Line Items (${lineItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        FilledTonalButton(onClick = { onAddLineItem(est.id) }) { Icon(Icons.Default.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add Item") }
                    }
                }
                
                if (lineItems.isEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ListAlt, null, Modifier.size(48.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(8.dp))
                                    Text("No line items yet", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(8.dp))
                                    Button(onClick = { onAddLineItem(est.id) }) { Text("Add First Item") }
                                }
                            }
                        }
                    }
                } else {
                    items(lineItems, key = { it.id }) { item ->
                        LineItemCard(item = item, onEdit = { }, onDelete = { viewModel.deleteLineItem(item) }, onToggleSelected = { viewModel.toggleOptionalItem(item.id, it) })
                    }
                }
                
                item { EstimateTotalsCard(est.subtotal, est.taxRate, est.taxAmount, est.discount, est.total, est.depositRequired) }
                
                if (est.terms.isNotEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Terms & Conditions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(8.dp))
                                Text(est.terms, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}