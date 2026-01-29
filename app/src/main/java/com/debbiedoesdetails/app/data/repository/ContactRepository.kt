package com.debbiedoesdetails.app.data.repository

import com.debbiedoesdetails.app.data.local.Address
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.local.ContactDao
import com.debbiedoesdetails.app.data.local.AddressDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ContactRepository(
    private val contactDao: ContactDao,
    private val addressDao: AddressDao
) {

    // ===== CONTACT OPERATIONS =====

    suspend fun addContact(contact: Contact): Long {
        return contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(id: Long) {
        val contact = contactDao.getContactById(id)
        if (contact != null) {
            contactDao.deleteContact(contact)
        }
    }

    fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
    }

    suspend fun allContactsSnapshot(): List<Contact> {
        return contactDao.getAllContacts().first()
    }

    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)
    }

    suspend fun getContactByName(name: String): Contact? {
        return contactDao.getContactByName(name)
    }

    fun getDuplicates(): Flow<List<Contact>> {
        return contactDao.getDuplicates()
    }

    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts(query)
    }

    // ===== ADDRESS OPERATIONS =====

    suspend fun addAddress(address: Address): Long {
        return addressDao.insertAddress(address)
    }

    suspend fun updateAddress(address: Address) {
        addressDao.updateAddress(address)
    }

    suspend fun deleteAddress(id: Long) {
        val address = addressDao.getAddressById(id)
        if (address != null) {
            addressDao.deleteAddress(address)
        }
    }

    fun getAddressesByContact(contactId: Long): Flow<List<Address>> {
        return addressDao.getAddressesByContact(contactId)
    }

    suspend fun getAddressById(id: Long): Address? {
        return addressDao.getAddressById(id)
    }

    // ===== MERGE & DUPLICATE OPERATIONS =====

    suspend fun mergeContacts(primaryId: Long, duplicateId: Long, isPlaceholder: Boolean = false) {
        val primary = contactDao.getContactById(primaryId) ?: return
        val duplicate = contactDao.getContactById(duplicateId) ?: return

        // Combine phone numbers
        val mergedPhones = (primary.phones + duplicate.phones)
            .distinct()
            .filter { it.isNotEmpty() }

        // Combine emails
        val mergedEmails = (primary.emails + duplicate.emails)
            .distinct()
            .filter { it.isNotEmpty() }

        // Combine fax numbers
        val mergedFax = (primary.fax + duplicate.fax)
            .distinct()
            .filter { it.isNotEmpty() }

        // Combine social media
        val mergedSocial = primary.socialMedia + duplicate.socialMedia

        // Update primary with merged data
        val mergedContact = primary.copy(
            phones = mergedPhones,
            emails = mergedEmails,
            fax = mergedFax,
            socialMedia = mergedSocial,
            isPlaceholder = isPlaceholder,
            notes = if (isPlaceholder) "Placeholder: Awaiting identification" else primary.notes,
            updatedAt = java.time.LocalDateTime.now()
        )

        contactDao.updateContact(mergedContact)

        // Move addresses from duplicate to primary
        val duplicateAddresses = addressDao.getAddressesByContact(duplicateId)
        duplicateAddresses.collect { addresses ->
            addresses.forEach { address ->
                val newAddress = address.copy(contactId = primaryId)
                addressDao.updateAddress(newAddress)
            }
        }

        // Delete duplicate contact (cascade deletes its addresses)
        contactDao.deleteContact(duplicate)
    }

    suspend fun markAsPlaceholder(contactId: Long, reason: String = "") {
        val contact = contactDao.getContactById(contactId) ?: return
        val updated = contact.copy(
            isPlaceholder = true,
            notes = reason.ifEmpty { "Placeholder: Awaiting identification" },
            updatedAt = java.time.LocalDateTime.now()
        )
        contactDao.updateContact(updated)
    }

    suspend fun unmarkPlaceholder(contactId: Long) {
        val contact = contactDao.getContactById(contactId) ?: return
        val updated = contact.copy(
            isPlaceholder = false,
            notes = "",
            updatedAt = java.time.LocalDateTime.now()
        )
        contactDao.updateContact(updated)
    }

    // ===== UTILITY OPERATIONS =====

    suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }

    fun findDuplicate(contact: Contact): Contact? {
        // This would be better as a database query, but for now
        // we'll keep it simple - in production use a proper search
        return null
    }
}