package com.debbiedoesit.antigravity.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "contact_interactions",
        foreignKeys =
                [
                        ForeignKey(
                                entity = Contact::class,
                                parentColumns = ["id"],
                                childColumns = ["contactId"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index(value = ["contactId"])]
)
data class ContactInteraction(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val contactId: Long,
        val timestamp: Long = System.currentTimeMillis(),
        val type: String, // 'call', 'email', 'ai_interaction', etc.
        val summary: String,
        val sentiment: String = "neutral",
        val remoteId: Long? = null // For syncing with backend
)
