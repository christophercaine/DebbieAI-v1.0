package com.debbiedoesit.debbieai.contacts.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    
    // ===== QUERIES =====
    
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE isDuplicate = 0 ORDER BY name ASC")
    fun getNonDuplicateContacts(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE isDuplicate = 1 ORDER BY name ASC")
    fun getDuplicates(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE contactType = :type ORDER BY name ASC")
    fun getByType(type: ContactType): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getById(id: Long): Contact?
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<Contact?>
    
    @Query("SELECT * FROM contacts WHERE deviceContactId = :deviceId LIMIT 1")
    suspend fun getByDeviceId(deviceId: String): Contact?
    
    // Search
    @Query("""
        SELECT * FROM contacts 
        WHERE name LIKE '%' || :query || '%' 
           OR company LIKE '%' || :query || '%'
           OR notes LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun search(query: String): Flow<List<Contact>>
    
    // For sync - get all as snapshot
    @Query("SELECT * FROM contacts")
    suspend fun getAllSnapshot(): List<Contact>
    
    // ===== INSERTS =====
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<Contact>): List<Long>
    
    // ===== UPDATES =====
    
    @Update
    suspend fun update(contact: Contact)
    
    @Query("UPDATE contacts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
    
    @Query("UPDATE contacts SET isDuplicate = :isDuplicate, isDuplicateOf = :isDuplicateOf WHERE id = :id")
    suspend fun updateDuplicateStatus(id: Long, isDuplicate: Boolean, isDuplicateOf: Long?)
    
    @Query("UPDATE contacts SET contactType = :type WHERE id = :id")
    suspend fun updateContactType(id: Long, type: ContactType)
    
    @Query("UPDATE contacts SET lastContactedAt = :timestamp WHERE id = :id")
    suspend fun updateLastContacted(id: Long, timestamp: java.time.LocalDateTime)
    
    // ===== DELETES =====
    
    @Delete
    suspend fun delete(contact: Contact)
    
    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM contacts WHERE isDuplicate = 1")
    suspend fun deleteAllDuplicates()
    
    // ===== COUNTS =====
    
    @Query("SELECT COUNT(*) FROM contacts")
    fun getCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM contacts WHERE isDuplicate = 1")
    fun getDuplicateCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM contacts WHERE contactType = :type")
    fun getCountByType(type: ContactType): Flow<Int>
}

@Dao
interface AddressDao {
    
    @Query("SELECT * FROM addresses WHERE contactId = :contactId ORDER BY isPrimary DESC")
    fun getAddressesForContact(contactId: Long): Flow<List<Address>>
    
    @Query("SELECT * FROM addresses WHERE contactId = :contactId")
    suspend fun getAddressesSnapshot(contactId: Long): List<Address>
    
    @Query("SELECT * FROM addresses WHERE id = :id")
    suspend fun getById(id: Long): Address?
    
    @Query("SELECT * FROM addresses WHERE isJobSite = 1")
    fun getJobSites(): Flow<List<Address>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(addresses: List<Address>): List<Long>
    
    @Update
    suspend fun update(address: Address)
    
    @Delete
    suspend fun delete(address: Address)
    
    @Query("DELETE FROM addresses WHERE contactId = :contactId")
    suspend fun deleteAllForContact(contactId: Long)
    
    @Query("UPDATE addresses SET isPrimary = 0 WHERE contactId = :contactId")
    suspend fun clearPrimaryForContact(contactId: Long)
}
