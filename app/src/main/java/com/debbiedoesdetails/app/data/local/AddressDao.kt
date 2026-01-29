package com.debbiedoesdetails.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Insert
    suspend fun insertAddress(address: Address): Long

    @Update
    suspend fun updateAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("SELECT * FROM addresses WHERE contactId = :contactId ORDER BY isPrimary DESC")
    fun getAddressesByContact(contactId: Long): Flow<List<Address>>

    @Query("SELECT * FROM addresses WHERE id = :id")
    suspend fun getAddressById(id: Long): Address?

    @Query("DELETE FROM addresses WHERE contactId = :contactId")
    suspend fun deleteAddressesByContact(contactId: Long)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddressById(id: Long)
}