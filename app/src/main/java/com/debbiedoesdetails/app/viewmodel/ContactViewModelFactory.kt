package com.debbiedoesdetails.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesdetails.app.data.local.ContactDatabase
import com.debbiedoesdetails.app.data.repository.ContactRepository
import com.debbiedoesdetails.app.data.sync.ContactSyncService

class ContactViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            // Get database instance
            val database = ContactDatabase.getDatabase(context)
            
            // Create repository
            val repository = ContactRepository(
                contactDao = database.contactDao(),
                addressDao = database.addressDao()
            )
            
            // Create sync service
            val syncService = ContactSyncService(
                context = context,
                repository = repository
            )
            
            return ContactViewModel(repository, syncService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}