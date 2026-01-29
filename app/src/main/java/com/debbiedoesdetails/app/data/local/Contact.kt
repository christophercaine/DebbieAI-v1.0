package com.debbiedoesdetails.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Contact categories for contractors
 */
object ContactCategory {
    const val CUSTOMER = "Customer"
    const val LEAD = "Lead"
    const val VENDOR = "Vendor"
    const val SUBCONTRACTOR = "Subcontractor"
    const val EMPLOYEE = "Employee"
    const val PERSONAL = "Personal"
    const val OTHER = "Other"
    
    val ALL = listOf(CUSTOMER, LEAD, VENDOR, SUBCONTRACTOR, EMPLOYEE, PERSONAL, OTHER)
}

/**
 * Common tags for contractor contacts
 */
object ContactTags {
    const val VIP = "VIP"
    const val FOLLOW_UP = "Follow Up"
    const val PENDING_ESTIMATE = "Pending Estimate"
    const val ACTIVE_JOB = "Active Job"
    const val PAST_CUSTOMER = "Past Customer"
    const val REFERRAL = "Referral"
    const val DO_NOT_CONTACT = "Do Not Contact"
    const val NEEDS_REVIEW = "Needs Review"
    
    val ALL = listOf(VIP, FOLLOW_UP, PENDING_ESTIMATE, ACTIVE_JOB, PAST_CUSTOMER, REFERRAL, DO_NOT_CONTACT, NEEDS_REVIEW)
}

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Basic Info
    val name: String,
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val company: String = "",
    val jobTitle: String = "",
    val website: String = "",
    val notes: String = "",
    
    // Category & Tags (AI-powered)
    val contactType: String = ContactCategory.PERSONAL,
    val category: String = ContactCategory.PERSONAL,  // AI-assigned category
    val tags: List<String> = emptyList(),             // AI-suggested tags
    
    // Name parts
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val nickname: String = "",
    
    // Personal
    val birthday: String = "",
    
    // Work
    val department: String = "",
    val companyPhone: String = "",
    val companyWebsite: String = "",
    
    // Address (legacy - use Address entity for multiple)
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "USA",
    
    // Communication
    val fax: List<String> = emptyList(),
    val socialMedia: Map<String, String> = emptyMap(),
    
    // AI & Smart Features
    val aiCategorized: Boolean = false,           // Has AI categorized this contact?
    val aiCategoryConfidence: Float = 0f,         // 0.0 to 1.0 confidence score
    val aiSuggestedTags: List<String> = emptyList(), // AI-suggested but not confirmed
    val aiNotes: String = "",                     // AI-generated insights
    val duplicateConfidence: Float = 0f,          // How confident is duplicate detection?
    val searchKeywords: List<String> = emptyList(), // AI-generated search keywords
    
    // Status flags
    val isPlaceholder: Boolean = false,
    val isDuplicate: Boolean = false,
    val isDuplicateOf: Long? = null,
    
    // Media
    val photoUrl: String = "",
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null,
    val lastContactedAt: LocalDateTime? = null,   // When did we last interact?
    val aiAnalyzedAt: LocalDateTime? = null       // When did AI last analyze?
)

data class ContactWithAddresses(
    val contact: Contact,
    val addresses: List<Address> = emptyList()
)

/**
 * Result from AI analysis
 */
data class AIContactAnalysis(
    val suggestedCategory: String,
    val categoryConfidence: Float,
    val suggestedTags: List<String>,
    val insights: String,
    val searchKeywords: List<String>,
    val isDuplicateOf: Long? = null,
    val duplicateConfidence: Float = 0f
)
