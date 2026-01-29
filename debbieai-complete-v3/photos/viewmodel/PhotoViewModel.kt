package com.debbiedoesit.debbieai.photos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.debbieai.core.database.Photo
import com.debbiedoesit.debbieai.core.database.Album
import com.debbiedoesit.debbieai.core.database.PhotoCategory
import com.debbiedoesit.debbieai.core.database.AlbumType
import com.debbiedoesit.debbieai.photos.data.repository.PhotoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PhotoViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    // ===== PHOTOS STATE =====
    
    val allPhotos = repository.getAllPhotos()
    val favoritePhotos = repository.getFavoritePhotos()
    val recentPhotos = repository.getRecentPhotos(50)
    val trashedPhotos = repository.getTrashedPhotos()
    
    private val _selectedPhotos = MutableStateFlow<Set<Long>>(emptySet())
    val selectedPhotos: StateFlow<Set<Long>> = _selectedPhotos.asStateFlow()
    
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()
    
    private val _currentPhoto = MutableStateFlow<Photo?>(null)
    val currentPhoto: StateFlow<Photo?> = _currentPhoto.asStateFlow()
    
    // ===== ALBUMS STATE =====
    
    val allAlbums = repository.getAllAlbums()
    val favoriteAlbums = repository.getFavoriteAlbums()
    
    private val _currentAlbum = MutableStateFlow<Album?>(null)
    val currentAlbum: StateFlow<Album?> = _currentAlbum.asStateFlow()
    
    private val _currentAlbumPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val currentAlbumPhotos: StateFlow<List<Photo>> = _currentAlbumPhotos.asStateFlow()
    
    // ===== FILTER STATE =====
    
    private val _filterCategory = MutableStateFlow<PhotoCategory?>(null)
    val filterCategory: StateFlow<PhotoCategory?> = _filterCategory.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Photo>>(emptyList())
    val searchResults: StateFlow<List<Photo>> = _searchResults.asStateFlow()
    
    // ===== VIEW MODE =====
    
    private val _viewMode = MutableStateFlow(PhotoViewMode.GRID)
    val viewMode: StateFlow<PhotoViewMode> = _viewMode.asStateFlow()
    
    private val _sortOrder = MutableStateFlow(PhotoSortOrder.DATE_DESC)
    val sortOrder: StateFlow<PhotoSortOrder> = _sortOrder.asStateFlow()
    
    // ===== IMPORT STATE =====
    
    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()
    
    // ===== PHOTO OPERATIONS =====
    
    fun loadPhoto(photoId: Long) {
        viewModelScope.launch {
            _currentPhoto.value = repository.getPhotoById(photoId)
        }
    }
    
    fun toggleFavorite(photoId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(photoId)
            // Refresh current photo if it's the one we toggled
            if (_currentPhoto.value?.id == photoId) {
                loadPhoto(photoId)
            }
        }
    }
    
    fun updateCategory(photoId: Long, category: PhotoCategory) {
        viewModelScope.launch {
            repository.getPhotoById(photoId)?.let { photo ->
                repository.updatePhoto(photo.copy(
                    category = category,
                    updatedAt = LocalDateTime.now()
                ))
                if (_currentPhoto.value?.id == photoId) {
                    loadPhoto(photoId)
                }
            }
        }
    }
    
    fun updateDescription(photoId: Long, description: String) {
        viewModelScope.launch {
            repository.getPhotoById(photoId)?.let { photo ->
                repository.updatePhoto(photo.copy(
                    description = description,
                    updatedAt = LocalDateTime.now()
                ))
                if (_currentPhoto.value?.id == photoId) {
                    loadPhoto(photoId)
                }
            }
        }
    }
    
    fun addTagToPhoto(photoId: Long, tag: String) {
        viewModelScope.launch {
            repository.getPhotoById(photoId)?.let { photo ->
                val newTags = photo.tags.toMutableList()
                if (!newTags.contains(tag)) {
                    newTags.add(tag)
                    repository.updatePhoto(photo.copy(
                        tags = newTags,
                        updatedAt = LocalDateTime.now()
                    ))
                    if (_currentPhoto.value?.id == photoId) {
                        loadPhoto(photoId)
                    }
                }
            }
        }
    }
    
    fun removeTagFromPhoto(photoId: Long, tag: String) {
        viewModelScope.launch {
            repository.getPhotoById(photoId)?.let { photo ->
                val newTags = photo.tags.toMutableList()
                newTags.remove(tag)
                repository.updatePhoto(photo.copy(
                    tags = newTags,
                    updatedAt = LocalDateTime.now()
                ))
                if (_currentPhoto.value?.id == photoId) {
                    loadPhoto(photoId)
                }
            }
        }
    }
    
    fun moveToTrash(photoId: Long) {
        viewModelScope.launch {
            repository.softDeletePhoto(photoId)
        }
    }
    
    fun restoreFromTrash(photoId: Long) {
        viewModelScope.launch {
            repository.restorePhoto(photoId)
        }
    }
    
    fun permanentlyDelete(photoId: Long) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
        }
    }
    
    fun linkToContact(photoId: Long, contactId: Long) {
        viewModelScope.launch {
            repository.linkPhotoToContact(photoId, contactId)
            if (_currentPhoto.value?.id == photoId) {
                loadPhoto(photoId)
            }
        }
    }
    
    fun linkBeforeAfter(beforeId: Long, afterId: Long) {
        viewModelScope.launch {
            repository.linkBeforeAfter(beforeId, afterId)
        }
    }
    
    // ===== SELECTION OPERATIONS =====
    
    fun toggleSelection(photoId: Long) {
        _selectedPhotos.value = _selectedPhotos.value.toMutableSet().apply {
            if (contains(photoId)) remove(photoId) else add(photoId)
        }
        _isSelectionMode.value = _selectedPhotos.value.isNotEmpty()
    }
    
    fun selectAll(photoIds: List<Long>) {
        _selectedPhotos.value = photoIds.toSet()
        _isSelectionMode.value = true
    }
    
    fun clearSelection() {
        _selectedPhotos.value = emptySet()
        _isSelectionMode.value = false
    }
    
    fun deleteSelected() {
        viewModelScope.launch {
            _selectedPhotos.value.forEach { photoId ->
                repository.softDeletePhoto(photoId)
            }
            clearSelection()
        }
    }
    
    fun moveSelectedToAlbum(albumId: Long) {
        viewModelScope.launch {
            _selectedPhotos.value.forEach { photoId ->
                repository.movePhotoToAlbum(photoId, albumId)
            }
            clearSelection()
        }
    }
    
    fun favoriteSelected() {
        viewModelScope.launch {
            _selectedPhotos.value.forEach { photoId ->
                repository.getPhotoById(photoId)?.let { photo ->
                    if (!photo.isFavorite) {
                        repository.toggleFavorite(photoId)
                    }
                }
            }
            clearSelection()
        }
    }
    
    // ===== ALBUM OPERATIONS =====
    
    fun loadAlbum(albumId: Long) {
        viewModelScope.launch {
            _currentAlbum.value = repository.getAlbumById(albumId)
            repository.getPhotosByAlbum(albumId).collect { photos ->
                _currentAlbumPhotos.value = photos
            }
        }
    }
    
    fun createAlbum(
        name: String,
        type: AlbumType = AlbumType.CUSTOM,
        description: String = ""
    ) {
        viewModelScope.launch {
            val album = Album(
                name = name,
                type = type,
                description = description
            )
            repository.createAlbum(album)
        }
    }
    
    fun updateAlbum(album: Album) {
        viewModelScope.launch {
            repository.updateAlbum(album)
            if (_currentAlbum.value?.id == album.id) {
                loadAlbum(album.id)
            }
        }
    }
    
    fun deleteAlbum(albumId: Long) {
        viewModelScope.launch {
            repository.deleteAlbum(albumId)
        }
    }
    
    fun toggleAlbumFavorite(albumId: Long) {
        viewModelScope.launch {
            repository.toggleAlbumFavorite(albumId)
        }
    }
    
    fun setAlbumCover(albumId: Long, photoId: Long) {
        viewModelScope.launch {
            repository.setAlbumCover(albumId, photoId)
        }
    }
    
    // ===== FILTER & SEARCH =====
    
    fun setFilterCategory(category: PhotoCategory?) {
        _filterCategory.value = category
    }
    
    fun searchPhotos(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                repository.searchPhotos(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
    
    // ===== VIEW OPTIONS =====
    
    fun setViewMode(mode: PhotoViewMode) {
        _viewMode.value = mode
    }
    
    fun setSortOrder(order: PhotoSortOrder) {
        _sortOrder.value = order
    }
    
    // ===== IMPORT =====
    
    fun setImportState(state: ImportState) {
        _importState.value = state
    }
    
    fun resetImportState() {
        _importState.value = ImportState.Idle
    }
}

// ===== ENUMS & DATA CLASSES =====

enum class PhotoViewMode {
    GRID,
    LIST,
    TIMELINE
}

enum class PhotoSortOrder {
    DATE_DESC,
    DATE_ASC,
    NAME_ASC,
    NAME_DESC,
    SIZE_DESC,
    SIZE_ASC
}

sealed class ImportState {
    object Idle : ImportState()
    data class Importing(val current: Int, val total: Int) : ImportState()
    data class Success(val imported: Int, val skipped: Int) : ImportState()
    data class Error(val message: String) : ImportState()
}
