package com.debbiedoesdetails.app.data.sync

import android.content.Context
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactSyncService(
    private val context: Context,
    private val repository: ContactRepository
) {
    suspend fun syncDeviceContacts() = withContext(Dispatchers.IO) {
        try {
            val appContacts = repository.allContactsSnapshot()

            // Check all app contacts for duplicates against each other
            for (contact in appContacts) {
                val duplicate = findDuplicate(contact, appContacts)
                if (duplicate != null && duplicate.id != contact.id) {
                    val updated = contact.copy(isDuplicate = true, isDuplicateOf = duplicate.id)
                    repository.updateContact(updated)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findDuplicate(contact: Contact, others: List<Contact>): Contact? {
        return others.firstOrNull { other ->
            other.id != contact.id && (
                    (contact.name.equals(other.name, ignoreCase = true) && contact.name.isNotEmpty()) ||
                            (contact.emails.any { email -> other.emails.contains(email) } && contact.emails.isNotEmpty()) ||
                            (contact.phones.any { phone -> other.phones.contains(phone) } && contact.phones.isNotEmpty())
                    )
        }
    }
}