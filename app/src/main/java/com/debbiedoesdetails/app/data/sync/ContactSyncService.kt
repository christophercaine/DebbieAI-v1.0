package com.debbiedoesdetails.app.data.sync

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ContactSyncService(
    private val context: Context,
    private val repository: ContactRepository
) {
    
    /**
     * Sync contacts from device to app database
     * - Reads all device contacts with phones and emails
     * - Checks for duplicates before inserting
     * - Updates existing contacts if they match
     */
    suspend fun syncDeviceContacts(): SyncResult = withContext(Dispatchers.IO) {
        val result = SyncResult()
        
        try {
            val deviceContacts = readDeviceContacts()
            val existingContacts = repository.allContactsSnapshot()
            
            for (deviceContact in deviceContacts) {
                val duplicate = findExistingMatch(deviceContact, existingContacts)
                
                when {
                    duplicate != null -> {
                        // Merge with existing contact
                        val merged = mergeContacts(duplicate, deviceContact)
                        repository.updateContact(merged)
                        result.updated++
                    }
                    else -> {
                        // Insert new contact
                        repository.addContact(deviceContact)
                        result.added++
                    }
                }
            }
            
            // Mark duplicates within app contacts
            markInternalDuplicates()
            
            result.success = true
        } catch (e: Exception) {
            e.printStackTrace()
            result.error = e.message
        }
        
        result
    }
    
    /**
     * Read all contacts from device using ContentResolver
     */
    private fun readDeviceContacts(): List<Contact> {
        val contacts = mutableMapOf<String, Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        // First pass: Get contact IDs and names
        val contactCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            ),
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        )
        
        contactCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(idIndex) ?: continue
                val displayName = cursor.getString(nameIndex) ?: "Unknown"
                
                contacts[contactId] = Contact(
                    name = displayName,
                    firstName = parseFirstName(displayName),
                    lastName = parseLastName(displayName),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            }
        }
        
        // Second pass: Get phone numbers
        val phoneCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            ),
            null,
            null,
            null
        )
        
        phoneCursor?.use { cursor ->
            val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIndex) ?: continue
                val phoneNumber = cursor.getString(numberIndex)?.normalizePhone() ?: continue
                
                contacts[contactId]?.let { contact ->
                    if (phoneNumber.isNotEmpty() && phoneNumber !in contact.phones) {
                        contacts[contactId] = contact.copy(
                            phones = contact.phones + phoneNumber
                        )
                    }
                }
            }
        }
        
        // Third pass: Get email addresses
        val emailCursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS
            ),
            null,
            null,
            null
        )
        
        emailCursor?.use { cursor ->
            val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIndex) ?: continue
                val email = cursor.getString(emailIndex)?.trim()?.lowercase() ?: continue
                
                contacts[contactId]?.let { contact ->
                    if (email.isNotEmpty() && email !in contact.emails) {
                        contacts[contactId] = contact.copy(
                            emails = contact.emails + email
                        )
                    }
                }
            }
        }
        
        // Fourth pass: Get organization info
        val orgCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Organization.COMPANY,
                ContactsContract.CommonDataKinds.Organization.TITLE
            ),
            "${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
            null
        )
        
        orgCursor?.use { cursor ->
            val contactIdIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val companyIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
            val titleIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)
            
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIndex) ?: continue
                val company = cursor.getString(companyIndex) ?: ""
                val title = cursor.getString(titleIndex) ?: ""
                
                contacts[contactId]?.let { contact ->
                    contacts[contactId] = contact.copy(
                        company = company,
                        jobTitle = title
                    )
                }
            }
        }
        
        return contacts.values.toList()
    }
    
    /**
     * Find an existing contact that matches the device contact
     */
    private fun findExistingMatch(deviceContact: Contact, existing: List<Contact>): Contact? {
        return existing.firstOrNull { existingContact ->
            // Match by name (case insensitive)
            val nameMatch = deviceContact.name.equals(existingContact.name, ignoreCase = true) &&
                    deviceContact.name.isNotEmpty()
            
            // Match by any shared phone number
            val phoneMatch = deviceContact.phones.any { phone ->
                existingContact.phones.any { it.normalizePhone() == phone.normalizePhone() }
            } && deviceContact.phones.isNotEmpty()
            
            // Match by any shared email
            val emailMatch = deviceContact.emails.any { email ->
                existingContact.emails.any { it.equals(email, ignoreCase = true) }
            } && deviceContact.emails.isNotEmpty()
            
            nameMatch || phoneMatch || emailMatch
        }
    }
    
    /**
     * Merge a device contact into an existing app contact
     */
    private fun mergeContacts(existing: Contact, device: Contact): Contact {
        return existing.copy(
            // Keep existing name unless empty
            name = existing.name.ifEmpty { device.name },
            firstName = existing.firstName.ifEmpty { device.firstName },
            lastName = existing.lastName.ifEmpty { device.lastName },
            
            // Merge phones (deduplicated)
            phones = (existing.phones + device.phones)
                .map { it.normalizePhone() }
                .distinct()
                .filter { it.isNotEmpty() },
            
            // Merge emails (deduplicated, lowercase)
            emails = (existing.emails + device.emails)
                .map { it.trim().lowercase() }
                .distinct()
                .filter { it.isNotEmpty() },
            
            // Keep existing company/title unless empty
            company = existing.company.ifEmpty { device.company },
            jobTitle = existing.jobTitle.ifEmpty { device.jobTitle },
            
            // Update timestamp
            updatedAt = LocalDateTime.now(),
            syncedAt = LocalDateTime.now()
        )
    }
    
    /**
     * Mark duplicate contacts within the app database
     */
    private suspend fun markInternalDuplicates() {
        val allContacts = repository.allContactsSnapshot()
        
        for (contact in allContacts) {
            if (contact.isDuplicate) continue // Already marked
            
            val duplicate = allContacts.firstOrNull { other ->
                other.id != contact.id &&
                other.id < contact.id && // Only mark newer as duplicate of older
                !other.isDuplicate &&
                (
                    // Same name
                    (contact.name.equals(other.name, ignoreCase = true) && contact.name.isNotEmpty()) ||
                    // Shared phone
                    contact.phones.any { phone -> 
                        other.phones.any { it.normalizePhone() == phone.normalizePhone() }
                    } ||
                    // Shared email
                    contact.emails.any { email ->
                        other.emails.any { it.equals(email, ignoreCase = true) }
                    }
                )
            }
            
            if (duplicate != null) {
                repository.updateContact(
                    contact.copy(
                        isDuplicate = true,
                        isDuplicateOf = duplicate.id,
                        updatedAt = LocalDateTime.now()
                    )
                )
            }
        }
    }
    
    // ===== UTILITY FUNCTIONS =====
    
    private fun parseFirstName(displayName: String): String {
        return displayName.split(" ").firstOrNull() ?: ""
    }
    
    private fun parseLastName(displayName: String): String {
        val parts = displayName.split(" ")
        return if (parts.size > 1) parts.last() else ""
    }
    
    private fun String.normalizePhone(): String {
        return this.filter { it.isDigit() }
    }
}

/**
 * Result of a sync operation
 */
data class SyncResult(
    var success: Boolean = false,
    var added: Int = 0,
    var updated: Int = 0,
    var error: String? = null
) {
    val total: Int get() = added + updated
    
    override fun toString(): String {
        return if (success) {
            "Sync complete: $added added, $updated updated"
        } else {
            "Sync failed: $error"
        }
    }
}