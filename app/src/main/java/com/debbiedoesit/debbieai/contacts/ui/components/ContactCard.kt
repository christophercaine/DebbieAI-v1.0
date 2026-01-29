package com.debbiedoesit.debbieai.contacts.ui.components

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
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.data.local.ContactType
import com.debbiedoesit.debbieai.core.ui.theme.*

/**
 * Contact card for grid/list display
 */
@Composable
fun ContactCard(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            ContactAvatar(
                name = contact.name,
                photoUri = contact.photoUri,
                contactType = contact.contactType,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    if (contact.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favorite",
                            tint = DebbieOrange,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(16.dp)
                        )
                    }
                }
                
                if (contact.company.isNotEmpty()) {
                    Text(
                        text = contact.company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Show first phone or email
                val subtitle = contact.phones.firstOrNull() 
                    ?: contact.emails.firstOrNull() 
                    ?: ""
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Type badge
            ContactTypeBadge(type = contact.contactType)
        }
    }
}

/**
 * Compact contact list item
 */
@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (contact.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Favorite",
                        tint = DebbieOrange,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                    )
                }
            }
        },
        supportingContent = {
            Text(
                text = contact.company.ifEmpty { 
                    contact.phones.firstOrNull() ?: contact.emails.firstOrNull() ?: "" 
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            ContactAvatar(
                name = contact.name,
                photoUri = contact.photoUri,
                contactType = contact.contactType,
                modifier = Modifier.size(40.dp)
            )
        },
        trailingContent = {
            ContactTypeBadge(type = contact.contactType, compact = true)
        },
        modifier = modifier.clickable { onClick() }
    )
}

/**
 * Contact avatar with initials or photo
 */
@Composable
fun ContactAvatar(
    name: String,
    photoUri: String?,
    contactType: ContactType,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" }
    
    val backgroundColor = when (contactType) {
        ContactType.CUSTOMER -> DebbieBlue
        ContactType.LEAD -> DebbieOrange
        ContactType.VENDOR -> DebbiePurple
        ContactType.SUBCONTRACTOR -> DebbieRed
        ContactType.CREW -> DebbieGreen
        ContactType.INSPECTOR -> DebbieTeal
        ContactType.REALTOR -> Color(0xFF9C27B0)
        ContactType.PROPERTY_MANAGER -> Color(0xFF795548)
        ContactType.OTHER -> Color.Gray
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Contact type badge chip
 */
@Composable
fun ContactTypeBadge(
    type: ContactType,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (type) {
        ContactType.CUSTOMER -> DebbieBlue to "Customer"
        ContactType.LEAD -> DebbieOrange to "Lead"
        ContactType.VENDOR -> DebbiePurple to "Vendor"
        ContactType.SUBCONTRACTOR -> DebbieRed to "Sub"
        ContactType.CREW -> DebbieGreen to "Crew"
        ContactType.INSPECTOR -> DebbieTeal to "Inspector"
        ContactType.REALTOR -> Color(0xFF9C27B0) to "Realtor"
        ContactType.PROPERTY_MANAGER -> Color(0xFF795548) to "PM"
        ContactType.OTHER -> Color.Gray to "Other"
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = if (compact) label.take(3) else label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Quick action buttons for contact
 */
@Composable
fun ContactQuickActions(
    contact: Contact,
    onCall: () -> Unit,
    onText: () -> Unit,
    onEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (contact.phones.isNotEmpty()) {
            FilledTonalIconButton(onClick = onCall) {
                Icon(Icons.Filled.Phone, contentDescription = "Call")
            }
            FilledTonalIconButton(onClick = onText) {
                Icon(Icons.Filled.Sms, contentDescription = "Text")
            }
        }
        if (contact.emails.isNotEmpty()) {
            FilledTonalIconButton(onClick = onEmail) {
                Icon(Icons.Filled.Email, contentDescription = "Email")
            }
        }
    }
}

/**
 * Tags display row
 */
@Composable
fun ContactTags(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tags.take(3).forEach { tag ->
            SuggestionChip(
                onClick = { },
                label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
            )
        }
        if (tags.size > 3) {
            Text(
                text = "+${tags.size - 3}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
