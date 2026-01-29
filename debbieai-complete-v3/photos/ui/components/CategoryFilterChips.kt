package com.debbiedoesit.debbieai.photos.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.core.database.PhotoCategory
import com.debbiedoesit.debbieai.core.ui.theme.PhotoCategoryColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChips(
    selectedCategory: PhotoCategory?,
    onCategorySelected: (PhotoCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedCategory == null) {
                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null
        )
        
        // Category chips
        PhotoCategory.values().forEach { category ->
            val color = PhotoCategoryColors.getColor(category)
            
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) },
                leadingIcon = {
                    Icon(
                        getCategoryIcon(category),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (selectedCategory == category) 
                            MaterialTheme.colorScheme.onSecondaryContainer 
                        else 
                            color
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.2f),
                    selectedLabelColor = color,
                    selectedLeadingIconColor = color
                )
            )
        }
    }
}

@Composable
fun ViewModeToggle(
    currentMode: ViewMode,
    onModeChanged: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ViewMode.values().forEach { mode ->
            IconButton(
                onClick = { onModeChanged(mode) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (currentMode == mode) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        Color.Transparent
                )
            ) {
                Icon(
                    imageVector = when (mode) {
                        ViewMode.GRID -> Icons.Filled.GridView
                        ViewMode.LIST -> Icons.Filled.ViewList
                        ViewMode.TIMELINE -> Icons.Filled.Timeline
                    },
                    contentDescription = mode.name,
                    tint = if (currentMode == mode)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

enum class ViewMode {
    GRID, LIST, TIMELINE
}

@Composable
fun SortOrderMenu(
    currentSort: SortOrder,
    onSortChanged: (SortOrder) -> Unit,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        SortOrder.values().forEach { sort ->
            DropdownMenuItem(
                text = { Text(sort.displayName) },
                onClick = {
                    onSortChanged(sort)
                    onDismiss()
                },
                leadingIcon = {
                    Icon(
                        imageVector = sort.icon,
                        contentDescription = null
                    )
                },
                trailingIcon = if (currentSort == sort) {
                    { Icon(Icons.Filled.Check, contentDescription = "Selected") }
                } else null
            )
        }
    }
}

enum class SortOrder(val displayName: String, val icon: ImageVector) {
    DATE_DESC("Newest First", Icons.Filled.ArrowDownward),
    DATE_ASC("Oldest First", Icons.Filled.ArrowUpward),
    NAME_ASC("Name A-Z", Icons.Filled.SortByAlpha),
    NAME_DESC("Name Z-A", Icons.Filled.SortByAlpha),
    SIZE_DESC("Largest First", Icons.Filled.PhotoSizeSelectLarge),
    SIZE_ASC("Smallest First", Icons.Filled.PhotoSizeSelectSmall)
}

// Helper functions
private fun getCategoryIcon(category: PhotoCategory): ImageVector = when (category) {
    PhotoCategory.BEFORE -> Icons.Filled.History
    PhotoCategory.DURING -> Icons.Filled.Construction
    PhotoCategory.AFTER -> Icons.Filled.CheckCircle
    PhotoCategory.DAMAGE -> Icons.Filled.Warning
    PhotoCategory.MATERIALS -> Icons.Filled.Inventory
    PhotoCategory.MEASUREMENT -> Icons.Filled.Straighten
    PhotoCategory.RECEIPT -> Icons.Filled.Receipt
    PhotoCategory.REFERENCE -> Icons.Filled.Info
    PhotoCategory.GENERAL -> Icons.Filled.Photo
}

private val PhotoCategory.displayName: String
    get() = when (this) {
        PhotoCategory.BEFORE -> "Before"
        PhotoCategory.DURING -> "During"
        PhotoCategory.AFTER -> "After"
        PhotoCategory.DAMAGE -> "Damage"
        PhotoCategory.MATERIALS -> "Materials"
        PhotoCategory.MEASUREMENT -> "Measure"
        PhotoCategory.RECEIPT -> "Receipt"
        PhotoCategory.REFERENCE -> "Reference"
        PhotoCategory.GENERAL -> "General"
    }
