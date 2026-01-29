package com.debbiedoesit.debbieai.contacts.data.repository

import com.debbiedoesit.debbieai.contacts.data.local.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ContactRepository(
    private val contactDao: ContactDao,
    private val addressDao: AddressDao
) {
    // ===== CONTACT QUERIES =====
    
    fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()
    
    fun getNonDuplicateContacts(): Flow<List<Contact>> = contactDao.getNonDuplicateContacts()
    
    fun getDuplicates(): Flow<List<Contact>> = contactDao.getDuplicates()
    
    fun getFavorites(): Flow<List<Contact>> = contactDao.getFavorites()
    
    fun getByType(type: ContactType): Flow<List<Contact>> = contactDao.getByType(type)
    
    suspend fun getContactById(id: Long): Contact? = contactDao.getById(id)
    
    fun getContactByIdFlow(id: Long): Flow<Contact?> = contactDao.getByIdFlow(id)
    
    fun searchContacts(query: String): Flow<List<Contact>> = contactDao.search(query)
    
    suspend fun allContactsSnapshot(): List<Contact> = contactDao.getAllSnapshot()
    
    // ===== CONTACT MUTATIONS =====
    
    suspend fun addContact(contact: Contact): Long {
        return contactDao.insert(contact)
    }
    
    suspend fun updateContact(contact: Contact) {
        contactDao.update(contact.copy(updatedAt = LocalDateTime.now()))
    }
    
    suspend fun deleteContact(id: Long) {
        contactDao.deleteById(id)
    }
    
    suspend fun toggleFavorite(id: Long) {
        val contact = contactDao.getById(id)
        contact?.let {
            contactDao.updateFavorite(id, !it.isFavorite)
        }
    }
    
    suspend fun setContactType(id: Long, type: ContactType) {
        contactDao.updateContactType(id, type)
    }
    
    suspend fun recordContact(id: Long) {
        contactDao.updateLastContacted(id, LocalDateTime.now())
    }
    
    // ===== DUPLICATE HANDLING =====
    
    suspend fun markAsDuplicate(contactId: Long, duplicateOfId: Long) {
        contactDao.updateDuplicateStatus(contactId, true, duplicateOfId)
    }
    
    suspend fun dismissDuplicate(contactId: Long) {
        contactDao.updateDuplicateStatus(contactId, false, null)
    }
    
    suspend fun mergeContacts(primaryId: Long, duplicateId: Long) {
        val primary = contactDao.getById(primaryId) ?: return
        val duplicate = contactDao.getById(duplicateId) ?: return
        
        // Merge phones (deduplicated)
        val mergedPhones = (primary.phones + duplicate.phones).distinct()
        
        // Merge emails (deduplicated)
        val mergedEmails = (primary.emails + duplicate.emails).distinct()
        
        // Merge tags
        val mergedTags = (primary.tags + duplicate.tags).distinct()
        
        // Merge social media
        val mergedSocial = primary.socialMedia + duplicate.socialMedia
        
        // Update primary with merged data
        val merged = primary.copy(
            phones = mergedPhones,
            emails = mergedEmails,
            tags = mergedTags,
            socialMedia = mergedSocial,
            company = primary.company.ifEmpty { duplicate.company },
            jobTitle = primary.jobTitle.ifEmpty { duplicate.jobTitle },
            notes = if (duplicate.notes.isNotEmpty() && duplicate.notes != primary.notes) {
                "${primary.notes}\n\n[Merged from duplicate]\n${duplicate.notes}".trim()
            } else primary.notes,
            updatedAt = LocalDateTime.now()
        )
        
        // Move addresses from duplicate to primary
        val duplicateAddresses = addressDao.getAddressesSnapshot(duplicateId)
        duplicateAddresses.forEach { address ->
            addressDao.insert(address.copy(id = 0, contactId = primaryId))
        }
        
        // Save merged and delete duplicate
        contactDao.update(merged)
        contactDao.deleteById(duplicateId)
    }
    
    // ===== ADDRESS OPERATIONS =====
    
    fun getAddressesForContact(contactId: Long): Flow<List<Address>> = 
        addressDao.getAddressesForContact(contactId)
    
    suspend fun addAddress(address: Address): Long {
        // If this is primary, clear other primaries first
        if (address.isPrimary) {
            addressDao.clearPrimaryForContact(address.contactId)
        }
        return addressDao.insert(address)
    }
    
    suspend fun updateAddress(address: Address) {
        if (address.isPrimary) {
            addressDao.clearPrimaryForContact(address.contactId)
        }
        addressDao.update(address)
    }
    
    suspend fun deleteAddress(address: Address) {
        addressDao.delete(address)
    }
    
    fun getJobSites(): Flow<List<Address>> = addressDao.getJobSites()
    
    // ===== COUNTS =====
    
    fun getContactCount(): Flow<Int> = contactDao.getCount()
    
    fun getDuplicateCount(): Flow<Int> = contactDao.getDuplicateCount()
}
