package com.debbiedoesdetails.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.repository.ContactRepository
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {
    val contacts = repository.allContacts

    fun loadContacts() {
        viewModelScope.launch {
            repository.allContactsSnapshot()
        }
    }

    fun addContact(name: String, email: String, phone: String, company: String) {
        viewModelScope.launch {
            val contact = Contact(
                name = name,
                emails = listOf(email),
                phones = listOf(phone),
                company = company
            )
            repository.addContact(contact)
            loadContacts()
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact)
            loadContacts()
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            repository.deleteContact(id)
            loadContacts()
        }
    }

    fun mergeContacts(primary: Contact, duplicate: Contact) {
        viewModelScope.launch {
            repository.mergeContacts(primary, duplicate)
            loadContacts()
        }
    }

    fun syncWithServer() {
        viewModelScope.launch {
            repository.syncWithServer()
            loadContacts()
        }
    }
}