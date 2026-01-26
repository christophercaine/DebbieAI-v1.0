package com.debbiedoesdetails.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE contactId = :contactId ORDER BY isPrimary DESC")
    fun getAddressesByContact(contactId: String): Flow<List<Address>>

    @Query("SELECT * FROM addresses WHERE id = :id")
    suspend fun getAddressById(id: String): Address?

    @Insert
    suspend fun insertAddress(address: Address): Long

    @Update
    suspend fun updateAddress(address: Address)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddressById(id: String)

    @Query("DELETE FROM addresses WHERE contactId = :contactId")
    suspend fun deleteAddressesByContact(contactId: String)
}