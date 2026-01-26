package com.debbiedoesdetails.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Address(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val contactId: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String = "USA",
    val addressType: String = "Home",
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)