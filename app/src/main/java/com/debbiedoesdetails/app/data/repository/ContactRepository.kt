package com.debbiedoesdetails.app.data.repository

import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.local.ContactDao
import com.debbiedoesdetails.app.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ContactRepository(
    private val dao: ContactDao,
    private val apiService: ApiService
) {
    val allContacts: Flow<List<Contact>> = dao.getAllContacts()

    suspend fun allContactsSnapshot(): List<Contact> = withContext(Dispatchers.IO) {
        try {
            allContacts.first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addContact(contact: Contact): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.insertContact(contact)
            try {
                apiService.createContact(contact)
            } catch (e: Exception) {
                // Offline - will sync later
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContact(contact: Contact): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.updateContact(contact)
            try {
                apiService.updateContact(contact.id, contact)
            } catch (e: Exception) {
                // Offline - will sync later
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.deleteContactById(id)
            try {
                apiService.deleteContact(id)
            } catch (e: Exception) {
                // Offline - will sync later
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mergeContacts(primary: Contact, duplicate: Contact, isPlaceholder: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val mergedPhones = (primary.phones + duplicate.phones).distinct().filter { it.isNotEmpty() }
            val mergedEmails = (primary.emails + duplicate.emails).distinct().filter { it.isNotEmpty() }

            val merged = Contact(
                id = primary.id,
                name = primary.name.ifEmpty { duplicate.name },
                phones = mergedPhones,
                emails = mergedEmails,
                company = primary.company.ifEmpty { duplicate.company },
                jobTitle = primary.jobTitle.ifEmpty { duplicate.jobTitle },
                notes = "",
                isPlaceholder = isPlaceholder,
                isDuplicate = false,
                duplicateOf = null
            )

            dao.updateContact(merged)
            dao.deleteContactById(duplicate.id)

            try {
                apiService.updateContact(merged.id, merged)
                apiService.deleteContact(duplicate.id)
            } catch (e: Exception) {
                // Offline - will sync later
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val contacts = apiService.getAllContacts()
            contacts.forEach { dao.insertContact(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}