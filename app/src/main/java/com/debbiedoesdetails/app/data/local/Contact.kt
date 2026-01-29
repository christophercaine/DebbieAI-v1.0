package com.debbiedoesdetails.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val company: String = "",
    val jobTitle: String = "",
    val website: String = "",
    val notes: String = "",
    val contactType: String = "Personal",
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val nickname: String = "",
    val birthday: String = "",
    val department: String = "",
    val companyPhone: String = "",
    val companyWebsite: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "USA",
    val fax: List<String> = emptyList(),
    val socialMedia: Map<String, String> = emptyMap(),
    val isPlaceholder: Boolean = false,
    val isDuplicate: Boolean = false,
    val isDuplicateOf: Long? = null,
    val photoUrl: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null
)

data class ContactWithAddresses(
    val contact: Contact,
    val addresses: List<Address> = emptyList()
)