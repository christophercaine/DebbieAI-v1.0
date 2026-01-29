package com.debbiedoesdetails.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.repository.ContactRepository
import com.debbiedoesdetails.app.data.sync.ContactSyncService
import com.debbiedoesdetails.app.data.sync.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel(
    private val repository: ContactRepository,
    private val syncService: ContactSyncService
) : ViewModel() {

    // All contacts (as Flow for automatic UI updates)
    val contacts = repository.getAllContacts()
    
    // Duplicate contacts
    val duplicates = repository.getDuplicates()
    
    // Sync state
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Search results
    private val _searchResults = MutableStateFlow<List<Contact>>(emptyList())
    val searchResults: StateFlow<List<Contact>> = _searchResults.asStateFlow()

    /**
     * Sync contacts from device
     */
    fun syncDeviceContacts() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            try {
                val result = syncService.syncDeviceContacts()
                _syncState.value = if (result.success) {
                    SyncState.Success(result)
                } else {
                    SyncState.Error(result.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }
    
    /**
     * Reset sync state to idle
     */
    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }

    /**
     * Add a new contact
     */
    fun addContact(
        name: String,
        phones: List<String> = emptyList(),
        emails: List<String> = emptyList(),
        company: String = "",
        jobTitle: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val contact = Contact(
                name = name,
                phones = phones.filter { it.isNotEmpty() },
                emails = emails.filter { it.isNotEmpty() },
                company = company,
                jobTitle = jobTitle,
                notes = notes
            )
            repository.addContact(contact)
        }
    }
    
    /**
     * Add contact with single phone/email (convenience method)
     */
    fun addContact(name: String, email: String, phone: String, company: String) {
        addContact(
            name = name,
            phones = if (phone.isNotEmpty()) listOf(phone) else emptyList(),
            emails = if (email.isNotEmpty()) listOf(email) else emptyList(),
            company = company
        )
    }

    /**
     * Update an existing contact
     */
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact.copy(
                updatedAt = java.time.LocalDateTime.now()
            ))
        }
    }

    /**
     * Delete a contact by ID
     */
    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }

    /**
     * Merge duplicate into primary contact
     */
    fun mergeContacts(primaryId: Long, duplicateId: Long) {
        viewModelScope.launch {
            repository.mergeContacts(primaryId, duplicateId)
        }
    }
    
    /**
     * Dismiss a duplicate (mark as not duplicate)
     */
    fun dismissDuplicate(contactId: Long) {
        viewModelScope.launch {
            val contact = repository.getContactById(contactId)
            if (contact != null) {
                repository.updateContact(contact.copy(
                    isDuplicate = false,
                    isDuplicateOf = null,
                    updatedAt = java.time.LocalDateTime.now()
                ))
            }
        }
    }
    
    /**
     * Get a single contact by ID
     */
    suspend fun getContactById(id: Long): Contact? {
        return repository.getContactById(id)
    }
    
    /**
     * Search contacts
     */
    fun searchContacts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                repository.searchContacts(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }
    
    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}

/**
 * Sync state for UI
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val result: SyncResult) : SyncState()
    data class Error(val message: String) : SyncState()
}