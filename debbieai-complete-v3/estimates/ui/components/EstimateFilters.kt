package com.debbiedoesit.debbieai.estimates.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.estimates.data.local.EstimateStatus
import com.debbiedoesit.debbieai.estimates.data.local.EstimateSummary

@Composable
fun EstimateStatusFilterChips(selectedStatus: EstimateStatus?, onStatusSelected: (EstimateStatus?) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = selectedStatus == null, onClick = { onStatusSelected(null) }, label = { Text("All") })
        EstimateStatus.entries.forEach { status ->
            FilterChip(selected = selectedStatus == status, onClick = { onStatusSelected(status) }, label = { Text(status.displayName) }, leadingIcon = if (selectedStatus == status) {{ Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }} else null)
        }
    }
}

@Composable
fun EstimateStatsBar(summary: EstimateSummary, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem(value = summary.totalEstimates.toString(), label = "Total")
            StatItem(value = summary.draftCount.toString(), label = "Drafts")
            StatItem(value = summary.sentCount.toString(), label = "Pending")
            StatItem(value = summary.approvedCount.toString(), label = "Approved")
            StatItem(value = "$${String.format("%,.0f", summary.approvedValue)}", label = "Value")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EstimateSortMenu(currentSort: String, onSortSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Date (Newest)" to "date_desc", "Date (Oldest)" to "date_asc", "Amount (High)" to "amount_desc", "Amount (Low)" to "amount_asc", "Status" to "status", "Title" to "title")
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) { Icon(Icons.Default.Sort, "Sort") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { (label, value) ->
                DropdownMenuItem(text = { Text(label) }, onClick = { onSortSelected(value); expanded = false }, leadingIcon = if (currentSort == value) {{ Icon(Icons.Default.Check, null) }} else null)
            }
        }
    }
}

@Composable
fun EstimateSearchBar(query: String, onQueryChange: (String) -> Unit, onClear: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = modifier.fillMaxWidth(), placeholder = { Text("Search estimates...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = onClear) { Icon(Icons.Default.Clear, "Clear") } }, singleLine = true)
}

@Composable
fun EstimateQuickActions(onCreateNew: () -> Unit, onViewDrafts: () -> Unit, onViewPending: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = onCreateNew, label = { Text("New Estimate") }, leadingIcon = { Icon(Icons.Default.Add, null, Modifier.size(18.dp)) }, modifier = Modifier.weight(1f))
        AssistChip(onClick = onViewDrafts, label = { Text("Drafts") }, leadingIcon = { Icon(Icons.Default.Edit, null, Modifier.size(18.dp)) })
        AssistChip(onClick = onViewPending, label = { Text("Pending") }, leadingIcon = { Icon(Icons.Default.Schedule, null, Modifier.size(18.dp)) })
    }
}

@Composable
fun EstimateTotalsCard(subtotal: Double, taxRate: Double, taxAmount: Double, discount: Double, total: Double, depositRequired: Double, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Estimate Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            HorizontalDivider()
            TotalRow("Subtotal", subtotal)
            TotalRow("Tax (${String.format("%.1f", taxRate * 100)}%)", taxAmount)
            if (discount > 0) TotalRow("Discount", -discount, MaterialTheme.colorScheme.error)
            HorizontalDivider()
            TotalRow("Total", total, MaterialTheme.colorScheme.primary, true)
            Spacer(Modifier.height(8.dp))
            TotalRow("Deposit Required", depositRequired, MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun TotalRow(label: String, amount: Double, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface, isBold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text("$${String.format("%,.2f", amount)}", style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}