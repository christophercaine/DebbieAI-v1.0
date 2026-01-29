package com.debbiedoesdetails.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesdetails.app.data.ai.AIService
import com.debbiedoesdetails.app.data.local.ContactDatabase
import com.debbiedoesdetails.app.data.repository.ContactRepository
import com.debbiedoesdetails.app.data.sync.ContactSyncService

class ContactViewModelFactory(
    private val context: Context,
    private val claudeApiKey: String = "" // Optional: Set your Claude API key for enhanced AI
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
            
            // Create AI service
            val aiService = AIService(
                apiKey = claudeApiKey
            )
            
            return ContactViewModel(repository, syncService, aiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
