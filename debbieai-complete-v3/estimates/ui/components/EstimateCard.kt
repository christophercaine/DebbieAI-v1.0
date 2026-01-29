package com.debbiedoesit.debbieai.estimates.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.estimates.data.local.*
import java.time.format.DateTimeFormatter

@Composable
fun EstimateCard(estimate: Estimate, contactName: String? = null, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                EstimateStatusChip(status = estimate.status)
                Text(text = "#${estimate.estimateNumber}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(text = estimate.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            contactName?.let { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) { Icon(Icons.Default.Person, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onSurfaceVariant); Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$${String.format("%,.2f", estimate.total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                estimate.validUntil?.let { date -> val isExpired = date.isBefore(java.time.LocalDate.now()); Text(if (isExpired) "Expired" else "Valid until ${date.format(DateTimeFormatter.ofPattern("MMM d"))}", style = MaterialTheme.typography.bodySmall, color = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
fun EstimateListItem(estimate: Estimate, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth().clickable(onClick = onClick), tonalElevation = 1.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, null, tint = getStatusColor(estimate.status))
            Column(Modifier.weight(1f)) {
                Text(estimate.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("#${estimate.estimateNumber} • ${estimate.status.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("$${String.format("%,.0f", estimate.total)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EstimateStatusChip(status: EstimateStatus, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = getStatusColor(status).copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
        Text(status.displayName, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = getStatusColor(status), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LineItemCard(item: EstimateLineItem, onEdit: () -> Unit, onDelete: () -> Unit, onToggleSelected: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.isOptional) Checkbox(item.isSelected, onToggleSelected)
                    LineItemCategoryChip(item.category)
                }
                Row { IconButton(onEdit, Modifier.size(32.dp)) { Icon(Icons.Default.Edit, "Edit", Modifier.size(18.dp)) }; IconButton(onDelete, Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Delete", Modifier.size(18.dp), MaterialTheme.colorScheme.error) } }
            }
            Text(item.description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (item.details.isNotEmpty()) Text(item.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.quantity} ${item.unit} × $${String.format("%.2f", item.unitPrice)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format("%,.2f", item.total)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = if (item.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.isOptional) Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.extraSmall) { Text("Optional", Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
                if (!item.isTaxable) Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.extraSmall) { Text("Non-taxable", Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}

@Composable
fun LineItemCategoryChip(category: LineItemCategory, modifier: Modifier = Modifier) {
    val color = when (category) { LineItemCategory.LABOR -> Color(0xFF2196F3); LineItemCategory.MATERIALS -> Color(0xFF4CAF50); LineItemCategory.EQUIPMENT -> Color(0xFFFF9800); LineItemCategory.SUBCONTRACTOR -> Color(0xFF9C27B0); LineItemCategory.PERMIT -> Color(0xFF607D8B); LineItemCategory.DISPOSAL -> Color(0xFF795548); LineItemCategory.OTHER -> Color(0xFF9E9E9E) }
    Surface(modifier, color.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall) { Text(category.displayName, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = color) }
}

@Composable
fun getStatusColor(status: EstimateStatus): Color = when (status) { EstimateStatus.DRAFT -> Color(0xFF9E9E9E); EstimateStatus.SENT -> Color(0xFF2196F3); EstimateStatus.VIEWED -> Color(0xFF00BCD4); EstimateStatus.APPROVED -> Color(0xFF4CAF50); EstimateStatus.REJECTED -> Color(0xFFF44336); EstimateStatus.EXPIRED -> Color(0xFFFF9800); EstimateStatus.CONVERTED -> Color(0xFF8BC34A) }