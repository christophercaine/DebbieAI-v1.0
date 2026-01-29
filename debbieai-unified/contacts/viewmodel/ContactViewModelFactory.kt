package com.debbiedoesit.debbieai.contacts.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesit.debbieai.core.database.DebbieDatabase
import com.debbiedoesit.debbieai.contacts.data.repository.ContactRepository
import com.debbiedoesit.debbieai.contacts.data.sync.ContactSyncService

class ContactViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            val database = DebbieDatabase.getDatabase(context)
            
            val repository = ContactRepository(
                contactDao = database.contactDao(),
                addressDao = database.addressDao()
            )
            
            val syncService = ContactSyncService(
                context = context,
                repository = repository
            )
            
            return ContactViewModel(repository, syncService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
