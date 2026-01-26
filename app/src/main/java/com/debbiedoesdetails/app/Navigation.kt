package com.debbiedoesdetails.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.ui.screens.AddContactScreen
import com.debbiedoesdetails.app.ui.screens.ContactDetailScreen
import com.debbiedoesdetails.app.ui.screens.ContactListScreen
import com.debbiedoesdetails.app.ui.screens.DuplicatesScreen
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

enum class Screen {
    LIST, ADD_CONTACT, DETAIL, DUPLICATES
}

@Composable
fun NavHost(viewModel: ContactViewModel, onRefresh: () -> Unit) {
    val currentScreen = remember { mutableStateOf(Screen.LIST) }
    val selectedContact = remember { mutableStateOf<Contact?>(null) }

    when (currentScreen.value) {
        Screen.LIST -> ContactListScreen(
            viewModel = viewModel,
            onAddClick = { currentScreen.value = Screen.ADD_CONTACT },
            onRefresh = onRefresh,
            onContactClick = { contact ->
                selectedContact.value = contact
                currentScreen.value = Screen.DETAIL
            },
            onDuplicatesClick = { currentScreen.value = Screen.DUPLICATES }
        )
        Screen.ADD_CONTACT -> AddContactScreen(
            viewModel = viewModel,
            onBackClick = {
                currentScreen.value = Screen.LIST
                viewModel.loadContacts()
            }
        )
        Screen.DETAIL -> {
            selectedContact.value?.let { contact ->
                ContactDetailScreen(
                    contact = contact,
                    viewModel = viewModel,
                    onBackClick = {
                        currentScreen.value = Screen.LIST
                        viewModel.loadContacts()
                    }
                )
            }
        }
        Screen.DUPLICATES -> DuplicatesScreen(
            viewModel = viewModel,
            onBackClick = { currentScreen.value = Screen.LIST }
        )
    }
}