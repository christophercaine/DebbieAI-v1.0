package com.debbiedoesit.antigravity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val phones: List<String> = emptyList(),
        val emails: List<String> = emptyList(),
        val company: String? = null,
        val jobTitle: String? = null,
        val notes: String? = null,
        val address: String? = null,
        val occupation: String? = null,
        val incomeLevel: String? = null,
        val assets: String? = null,
        val socialMediaJson: String? = null, // Stored as JSON string for simplicity in Room
        val lastAiSourceAt: Long? = null,
        val createdAt: Long = System.currentTimeMillis(),
        val updatedAt: Long = System.currentTimeMillis(),
        val remoteId: Long? = null // For syncing with backend
)
