package com.debbiedoesit.debbieai.contacts.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Address entity - multiple addresses per contact
 * Contractors need: home, billing, work site, property addresses
 */
@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId")]
)
data class Address(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val contactId: Long,
    
    // Address type
    val type: AddressType = AddressType.HOME,
    val label: String = "",  // Custom label if type is OTHER
    
    // Address components
    val street: String = "",
    val street2: String = "",  // Apt, Suite, etc.
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "USA",
    
    // GPS coordinates (for job sites)
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Flags
    val isPrimary: Boolean = false,
    val isJobSite: Boolean = false
)

/**
 * Address types for contractors
 */
enum class AddressType {
    HOME,           // Customer's home
    WORK,           // Customer's workplace
    BILLING,        // Where to send invoices
    PROPERTY,       // Property being worked on (may differ from home)
    JOB_SITE,       // Specific job location
    MAILING,        // Mailing address
    OTHER
}
