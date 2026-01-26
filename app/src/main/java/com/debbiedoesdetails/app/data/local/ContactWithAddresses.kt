package com.debbiedoesdetails.app.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ContactWithAddresses(
    @Embedded val contact: Contact,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val addresses: List<Address>
)