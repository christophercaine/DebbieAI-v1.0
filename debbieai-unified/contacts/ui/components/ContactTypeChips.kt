package com.debbiedoesit.debbieai.contacts.ui.components

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
import com.debbiedoesit.debbieai.contacts.data.local.ContactType
import com.debbiedoesit.debbieai.core.ui.theme.*

/**
 * Horizontal scrollable filter chips for contact types
 */
@Composable
fun ContactTypeFilterChips(
    selectedType: ContactType?,
    onTypeSelected: (ContactType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All filter
        FilterChip(
            selected = selectedType == null,
            onClick = { onTypeSelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedType == null) {
                { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(16.dp)) }
            } else null
        )
        
        // Type-specific filters
        ContactType.values().forEach { type ->
            val (icon, label, color) = getTypeInfo(type)
            
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(if (selectedType == type) null else type) },
                label = { Text(label) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedType == type) color else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.15f),
                    selectedLabelColor = color,
                    selectedLeadingIconColor = color
                )
            )
        }
    }
}

/**
 * Type selector dropdown for editing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactTypeSelector(
    selectedType: ContactType,
    onTypeSelected: (ContactType) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, label, color) = getTypeInfo(selectedType)
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = { },
            readOnly = true,
            label = { Text("Contact Type") },
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = color)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            ContactType.values().forEach { type ->
                val (typeIcon, typeLabel, typeColor) = getTypeInfo(type)
                
                DropdownMenuItem(
                    text = { Text(typeLabel) },
                    leadingIcon = {
                        Icon(typeIcon, contentDescription = null, tint = typeColor)
                    },
                    onClick = {
                        onTypeSelected(type)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

/**
 * Quick type chips for fast selection
 */
@Composable
fun QuickTypeChips(
    selectedType: ContactType,
    onTypeSelected: (ContactType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Most common types
        listOf(
            ContactType.CUSTOMER,
            ContactType.LEAD,
            ContactType.VENDOR,
            ContactType.SUBCONTRACTOR,
            ContactType.CREW
        ).forEach { type ->
            val (icon, label, color) = getTypeInfo(type)
            val isSelected = selectedType == type
            
            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                label = { Text(label) },
                leadingIcon = {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.15f),
                    selectedLabelColor = color
                )
            )
        }
    }
}

/**
 * Get icon, label and color for contact type
 */
private fun getTypeInfo(type: ContactType): Triple<ImageVector, String, Color> {
    return when (type) {
        ContactType.CUSTOMER -> Triple(Icons.Filled.Person, "Customer", DebbieBlue)
        ContactType.LEAD -> Triple(Icons.Filled.PersonAdd, "Lead", DebbieOrange)
        ContactType.VENDOR -> Triple(Icons.Filled.Store, "Vendor", DebbiePurple)
        ContactType.SUBCONTRACTOR -> Triple(Icons.Filled.Engineering, "Subcontractor", DebbieRed)
        ContactType.CREW -> Triple(Icons.Filled.Groups, "Crew", DebbieGreen)
        ContactType.INSPECTOR -> Triple(Icons.Filled.Verified, "Inspector", DebbieTeal)
        ContactType.REALTOR -> Triple(Icons.Filled.Home, "Realtor", Color(0xFF9C27B0))
        ContactType.PROPERTY_MANAGER -> Triple(Icons.Filled.Business, "Property Manager", Color(0xFF795548))
        ContactType.OTHER -> Triple(Icons.Filled.MoreHoriz, "Other", Color.Gray)
    }
}
