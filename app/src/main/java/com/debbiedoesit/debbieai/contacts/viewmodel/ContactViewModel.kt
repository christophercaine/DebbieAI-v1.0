package com.debbiedoesit.debbieai.contacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.debbieai.contacts.data.local.*
import com.debbiedoesit.debbieai.contacts.data.repository.ContactRepository
import com.debbiedoesit.debbieai.contacts.data.sync.ContactSyncService
import com.debbiedoesit.debbieai.contacts.data.sync.SyncResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ContactViewModel(
    private val repository: ContactRepository,
    private val syncService: ContactSyncService
) : ViewModel() {

    // All contacts
    val contacts = repository.getAllContacts()
    
    // Non-duplicate contacts (main list)
    val nonDuplicateContacts = repository.getNonDuplicateContacts()
    
    // Duplicates for review
    val duplicates = repository.getDuplicates()
    
    // Favorites
    val favorites = repository.getFavorites()
    
    // Sync state
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Contact>>(emptyList())
    val searchResults: StateFlow<List<Contact>> = _searchResults.asStateFlow()
    
    // Filter by type
    private val _typeFilter = MutableStateFlow<ContactType?>(null)
    val typeFilter: StateFlow<ContactType?> = _typeFilter.asStateFlow()
    
    // Filtered contacts
    val filteredContacts: Flow<List<Contact>> = combine(
        nonDuplicateContacts,
        typeFilter
    ) { contacts, filter ->
        if (filter == null) contacts
        else contacts.filter { it.contactType == filter }
    }
    
    // Selected contact for detail view
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()
    
    // Counts
    val contactCount = repository.getContactCount()
    val duplicateCount = repository.getDuplicateCount()

    // ===== SYNC =====
    
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
    
    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }

    // ===== CRUD =====
    
    fun addContact(
        name: String,
        phones: List<String> = emptyList(),
        emails: List<String> = emptyList(),
        company: String = "",
        jobTitle: String = "",
        notes: String = "",
        contactType: ContactType = ContactType.CUSTOMER,
        tags: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val contact = Contact(
                name = name,
                firstName = name.split(" ").firstOrNull() ?: "",
                lastName = name.split(" ").drop(1).joinToString(" "),
                phones = phones.filter { it.isNotBlank() },
                emails = emails.filter { it.isNotBlank() },
                company = company,
                jobTitle = jobTitle,
                notes = notes,
                contactType = contactType,
                tags = tags
            )
            repository.addContact(contact)
        }
    }
    
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact.copy(updatedAt = LocalDateTime.now()))
        }
    }
    
    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
        }
    }
    
    suspend fun getContactById(id: Long): Contact? {
        return repository.getContactById(id)
    }
    
    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    // ===== FAVORITES =====
    
    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }

    // ===== CONTACT TYPE =====
    
    fun setContactType(id: Long, type: ContactType) {
        viewModelScope.launch {
            repository.setContactType(id, type)
        }
    }
    
    fun setTypeFilter(type: ContactType?) {
        _typeFilter.value = type
    }

    // ===== DUPLICATES =====
    
    fun mergeContacts(primaryId: Long, duplicateId: Long) {
        viewModelScope.launch {
            repository.mergeContacts(primaryId, duplicateId)
        }
    }
    
    fun dismissDuplicate(contactId: Long) {
        viewModelScope.launch {
            repository.dismissDuplicate(contactId)
        }
    }

    // ===== SEARCH =====
    
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
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    // ===== RECORD INTERACTION =====
    
    fun recordContact(id: Long) {
        viewModelScope.launch {
            repository.recordContact(id)
        }
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val result: SyncResult) : SyncState()
    data class Error(val message: String) : SyncState()
}
