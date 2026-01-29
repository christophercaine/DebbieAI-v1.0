package com.debbiedoesdetails.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?

    @Query("SELECT * FROM contacts WHERE name = :name LIMIT 1")
    suspend fun getContactByName(name: String): Contact?

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    @Query("SELECT * FROM contacts WHERE isDuplicate = 1")
    fun getDuplicates(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%'")
    fun searchContacts(query: String): Flow<List<Contact>>
}