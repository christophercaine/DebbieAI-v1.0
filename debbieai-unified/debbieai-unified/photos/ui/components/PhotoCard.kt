package com.debbiedoesit.debbieai.photos.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.core.database.PhotoCategory
import com.debbiedoesit.debbieai.core.ui.theme.PhotoCategoryColors
import java.io.File
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = PhotoCategoryColors.getColor(photo.category)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(File(photo.filePath))
                    .crossfade(true)
                    .build(),
                contentDescription = photo.description.ifEmpty { photo.displayName },
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Selection overlay
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }
            
            // Selection checkbox
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.8f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Category badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = categoryColor.copy(alpha = 0.9f)
            ) {
                Text(
                    text = photo.category.name.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            
            // Bottom info bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date
                    Text(
                        text = photo.dateTaken?.format(DateTimeFormatter.ofPattern("MM/dd"))
                            ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    
                    // Icons for favorite, location, linked
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (photo.isFavorite) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Favorite",
                                tint = Color.Red,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        if (photo.hasLocation) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Has location",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        if (photo.contactId != null) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Linked to contact",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoListItem(
    photo: Photo,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryColor = PhotoCategoryColors.getColor(photo.category)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            // Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(File(photo.thumbnailPath ?: photo.filePath))
                    .crossfade(true)
                    .build(),
                contentDescription = photo.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = photo.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (photo.description.isNotEmpty()) {
                    Text(
                        text = photo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category chip
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = categoryColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = photo.category.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    // Date
                    photo.dateTaken?.let { date ->
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Action icons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (photo.isFavorite) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Favorite",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (photo.hasLocation) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Has location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
