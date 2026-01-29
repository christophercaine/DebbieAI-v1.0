package com.debbiedoesdetails.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesdetails.app.data.ai.AIService
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.local.ContactCategory
import com.debbiedoesdetails.app.data.repository.ContactRepository
import com.debbiedoesdetails.app.data.sync.ContactSyncService
import com.debbiedoesdetails.app.data.sync.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
<<<<<<< HEAD
import kotlinx.coroutines.flow.first
=======
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ContactViewModel(
    private val repository: ContactRepository,
<<<<<<< HEAD
    private val syncService: ContactSyncService,
    private val aiService: AIService = AIService()
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
    
    // AI Analysis state
    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()
    
    // Smart search mode
    private val _isSmartSearch = MutableStateFlow(true)
    val isSmartSearch: StateFlow<Boolean> = _isSmartSearch.asStateFlow()

=======
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

>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
    /**
     * Sync contacts from device
     */
    fun syncDeviceContacts() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            try {
                val result = syncService.syncDeviceContacts()
                _syncState.value = if (result.success) {
<<<<<<< HEAD
                    // After sync, analyze new contacts with AI
                    analyzeAllContactsInBackground()
=======
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
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
<<<<<<< HEAD
        notes: String = "",
        category: String = ContactCategory.PERSONAL,
        tags: List<String> = emptyList()
=======
        notes: String = ""
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
    ) {
        viewModelScope.launch {
            val contact = Contact(
                name = name,
                phones = phones.filter { it.isNotEmpty() },
                emails = emails.filter { it.isNotEmpty() },
                company = company,
                jobTitle = jobTitle,
<<<<<<< HEAD
                notes = notes,
                category = category,
                tags = tags
            )
            val id = repository.addContact(contact)
            
            // Analyze the new contact with AI
            analyzeContactInBackground(id)
=======
                notes = notes
            )
            repository.addContact(contact)
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
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
<<<<<<< HEAD
                updatedAt = LocalDateTime.now()
=======
                updatedAt = java.time.LocalDateTime.now()
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
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
<<<<<<< HEAD
                    duplicateConfidence = 0f,
                    updatedAt = LocalDateTime.now()
=======
                    updatedAt = java.time.LocalDateTime.now()
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
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
<<<<<<< HEAD
     * Smart search contacts - uses AI for natural language queries
=======
     * Search contacts
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
     */
    fun searchContacts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
<<<<<<< HEAD
            } else if (_isSmartSearch.value) {
                // Use AI-powered smart search
                val allContacts = contacts.first()
                _searchResults.value = aiService.parseSearchQuery(query, allContacts)
            } else {
                // Use basic search
=======
            } else {
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
                repository.searchContacts(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }
    
    /**
<<<<<<< HEAD
     * Toggle smart search mode
     */
    fun toggleSmartSearch() {
        _isSmartSearch.value = !_isSmartSearch.value
    }
    
    /**
=======
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
<<<<<<< HEAD
    
    // ===== AI Features =====
    
    /**
     * Analyze a single contact with AI
     */
    fun analyzeContact(contactId: Long) {
        viewModelScope.launch {
            _aiState.value = AIState.Analyzing
            try {
                val contact = repository.getContactById(contactId)
                if (contact != null) {
                    val analysis = aiService.analyzeContact(contact)
                    
                    // Update contact with AI analysis
                    repository.updateContact(contact.copy(
                        category = analysis.suggestedCategory,
                        aiCategorized = true,
                        aiCategoryConfidence = analysis.categoryConfidence,
                        aiSuggestedTags = analysis.suggestedTags,
                        aiNotes = analysis.insights,
                        searchKeywords = analysis.searchKeywords,
                        aiAnalyzedAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    ))
                    
                    _aiState.value = AIState.Success("Contact analyzed successfully")
                } else {
                    _aiState.value = AIState.Error("Contact not found")
                }
            } catch (e: Exception) {
                _aiState.value = AIState.Error(e.message ?: "Analysis failed")
            }
        }
    }
    
    /**
     * Analyze a contact in the background (no UI state changes)
     */
    private fun analyzeContactInBackground(contactId: Long) {
        viewModelScope.launch {
            try {
                val contact = repository.getContactById(contactId)
                if (contact != null && !contact.aiCategorized) {
                    val analysis = aiService.analyzeContact(contact)
                    repository.updateContact(contact.copy(
                        category = analysis.suggestedCategory,
                        aiCategorized = true,
                        aiCategoryConfidence = analysis.categoryConfidence,
                        aiSuggestedTags = analysis.suggestedTags,
                        aiNotes = analysis.insights,
                        searchKeywords = analysis.searchKeywords,
                        aiAnalyzedAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    ))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Analyze all contacts that haven't been analyzed yet
     */
    fun analyzeAllContacts() {
        viewModelScope.launch {
            _aiState.value = AIState.Analyzing
            try {
                val allContacts = contacts.first()
                val unanalyzed = allContacts.filter { !it.aiCategorized }
                var analyzed = 0
                
                for (contact in unanalyzed) {
                    try {
                        val analysis = aiService.analyzeContact(contact)
                        repository.updateContact(contact.copy(
                            category = analysis.suggestedCategory,
                            aiCategorized = true,
                            aiCategoryConfidence = analysis.categoryConfidence,
                            aiSuggestedTags = analysis.suggestedTags,
                            aiNotes = analysis.insights,
                            searchKeywords = analysis.searchKeywords,
                            aiAnalyzedAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        ))
                        analyzed++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                _aiState.value = AIState.Success("Analyzed $analyzed contacts")
            } catch (e: Exception) {
                _aiState.value = AIState.Error(e.message ?: "Analysis failed")
            }
        }
    }
    
    /**
     * Background analysis (no UI feedback)
     */
    private fun analyzeAllContactsInBackground() {
        viewModelScope.launch {
            try {
                val allContacts = contacts.first()
                val unanalyzed = allContacts.filter { !it.aiCategorized }
                
                for (contact in unanalyzed) {
                    try {
                        val analysis = aiService.analyzeContact(contact)
                        repository.updateContact(contact.copy(
                            category = analysis.suggestedCategory,
                            aiCategorized = true,
                            aiCategoryConfidence = analysis.categoryConfidence,
                            aiSuggestedTags = analysis.suggestedTags,
                            aiNotes = analysis.insights,
                            searchKeywords = analysis.searchKeywords,
                            aiAnalyzedAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        ))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Find duplicates with AI-powered fuzzy matching
     */
    fun findSmartDuplicates() {
        viewModelScope.launch {
            _aiState.value = AIState.Analyzing
            try {
                val allContacts = contacts.first()
                var duplicatesFound = 0
                
                for (contact in allContacts) {
                    if (contact.isDuplicate) continue
                    
                    val potentialDuplicates = aiService.findDuplicates(contact, allContacts)
                    
                    for ((duplicate, confidence) in potentialDuplicates) {
                        if (confidence > 0.7f && !duplicate.isDuplicate) {
                            repository.updateContact(duplicate.copy(
                                isDuplicate = true,
                                isDuplicateOf = contact.id,
                                duplicateConfidence = confidence,
                                updatedAt = LocalDateTime.now()
                            ))
                            duplicatesFound++
                        }
                    }
                }
                
                _aiState.value = AIState.Success("Found $duplicatesFound potential duplicates")
            } catch (e: Exception) {
                _aiState.value = AIState.Error(e.message ?: "Duplicate detection failed")
            }
        }
    }
    
    /**
     * Apply AI-suggested tags to a contact
     */
    fun applyAISuggestedTags(contactId: Long) {
        viewModelScope.launch {
            val contact = repository.getContactById(contactId)
            if (contact != null && contact.aiSuggestedTags.isNotEmpty()) {
                repository.updateContact(contact.copy(
                    tags = (contact.tags + contact.aiSuggestedTags).distinct(),
                    aiSuggestedTags = emptyList(),
                    updatedAt = LocalDateTime.now()
                ))
            }
        }
    }
    
    /**
     * Update contact category
     */
    fun updateContactCategory(contactId: Long, category: String) {
        viewModelScope.launch {
            val contact = repository.getContactById(contactId)
            if (contact != null) {
                repository.updateContact(contact.copy(
                    category = category,
                    contactType = category,
                    updatedAt = LocalDateTime.now()
                ))
            }
        }
    }
    
    /**
     * Add tag to contact
     */
    fun addTag(contactId: Long, tag: String) {
        viewModelScope.launch {
            val contact = repository.getContactById(contactId)
            if (contact != null && tag !in contact.tags) {
                repository.updateContact(contact.copy(
                    tags = contact.tags + tag,
                    updatedAt = LocalDateTime.now()
                ))
            }
        }
    }
    
    /**
     * Remove tag from contact
     */
    fun removeTag(contactId: Long, tag: String) {
        viewModelScope.launch {
            val contact = repository.getContactById(contactId)
            if (contact != null) {
                repository.updateContact(contact.copy(
                    tags = contact.tags - tag,
                    updatedAt = LocalDateTime.now()
                ))
            }
        }
    }
    
    /**
     * Reset AI state
     */
    fun resetAIState() {
        _aiState.value = AIState.Idle
    }
=======
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
}

/**
 * Sync state for UI
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val result: SyncResult) : SyncState()
    data class Error(val message: String) : SyncState()
<<<<<<< HEAD
}

/**
 * AI analysis state for UI
 */
sealed class AIState {
    object Idle : AIState()
    object Analyzing : AIState()
    data class Success(val message: String) : AIState()
    data class Error(val message: String) : AIState()
}
=======
}
>>>>>>> 41058dd7158f42aed2e175c365a9de945491adce
