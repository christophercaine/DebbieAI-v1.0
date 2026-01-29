package com.debbiedoesit.debbieai.contacts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Contact entity - core CRM data for contractors
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Basic info
    val name: String,
    val firstName: String = "",
    val lastName: String = "",
    
    // Multiple phones and emails stored as lists
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    
    // Work info
    val company: String = "",
    val jobTitle: String = "",
    
    // Contact type for contractors
    val contactType: ContactType = ContactType.CUSTOMER,
    
    // Quick tags for fast categorization
    val tags: List<String> = emptyList(),
    
    // Social media links
    val socialMedia: Map<String, String> = emptyMap(),
    
    // Notes and context
    val notes: String = "",
    
    // Profile photo (URI or path)
    val photoUri: String? = null,
    
    // Duplicate handling
    val isDuplicate: Boolean = false,
    val isDuplicateOf: Long? = null,
    
    // Placeholder for incomplete contacts
    val isPlaceholder: Boolean = false,
    
    // Favorite/starred
    val isFavorite: Boolean = false,
    
    // Source tracking
    val source: ContactSource = ContactSource.MANUAL,
    val deviceContactId: String? = null,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null,
    val lastContactedAt: LocalDateTime? = null
)

/**
 * Contact types for contractor CRM
 */
enum class ContactType {
    CUSTOMER,       // Paying customers
    LEAD,           // Potential customers
    VENDOR,         // Suppliers, material vendors
    SUBCONTRACTOR,  // Other contractors
    CREW,           // Your team members
    INSPECTOR,      // Building inspectors
    REALTOR,        // Real estate agents
    PROPERTY_MANAGER,
    OTHER
}

/**
 * Source of contact data
 */
enum class ContactSource {
    MANUAL,         // User entered
    DEVICE_SYNC,    // From phone contacts
    IMPORT,         // CSV/file import
    AI_DETECTED     // Auto-detected from documents
}
