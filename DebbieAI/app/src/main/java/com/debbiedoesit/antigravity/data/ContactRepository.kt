package com.debbiedoesit.antigravity.data

import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)
    }

    suspend fun insertContact(contact: Contact): Long {
        return contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    fun getInteractionsForContact(contactId: Long): Flow<List<ContactInteraction>> {
        return contactDao.getInteractionsForContact(contactId)
    }

    suspend fun insertInteraction(interaction: ContactInteraction): Long {
        return contactDao.insertInteraction(interaction)
    }

    // TODO: Implement actual network sync with backend/server.js
    suspend fun syncWithBackend() {
        // Placeholder for future Retrofit/Ktor implementation
    }
}
