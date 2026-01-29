package com.debbiedoesit.debbieai.contacts.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debbiedoesit.debbieai.contacts.ui.screens.*
import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel

/**
 * Contact module navigation routes
 */
object ContactRoutes {
    const val LIST = "contacts_list"
    const val DETAIL = "contacts_detail/{contactId}"
    const val ADD = "contacts_add"
    const val EDIT = "contacts_edit/{contactId}"
    const val SEARCH = "contacts_search"
    const val DUPLICATES = "contacts_duplicates"
    
    fun detail(contactId: Long) = "contacts_detail/$contactId"
    fun edit(contactId: Long) = "contacts_edit/$contactId"
}

/**
 * Contact module navigation graph
 */
@Composable
fun ContactNavigation(
    viewModel: ContactViewModel,
    navController: NavHostController = rememberNavController(),
    onSyncContacts: () -> Unit,
    startDestination: String = ContactRoutes.LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Contact List
        composable(ContactRoutes.LIST) {
            ContactListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(ContactRoutes.ADD) },
                onSyncContacts = onSyncContacts,
                onContactClick = { contact ->
                    navController.navigate(ContactRoutes.detail(contact.id))
                },
                onDuplicatesClick = { navController.navigate(ContactRoutes.DUPLICATES) },
                onSearchClick = { navController.navigate(ContactRoutes.SEARCH) }
            )
        }
        
        // Contact Detail
        composable(
            route = ContactRoutes.DETAIL,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0L
            val contacts by viewModel.contacts.collectAsState(initial = emptyList())
            val contact = contacts.find { it.id == contactId }
            
            if (contact != null) {
                ContactDetailScreen(
                    contact = contact,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { navController.navigate(ContactRoutes.edit(contact.id)) }
                )
            }
        }
        
        // Add Contact
        composable(ContactRoutes.ADD) {
            AddContactScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Edit Contact
        composable(
            route = ContactRoutes.EDIT,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0L
            val contacts by viewModel.contacts.collectAsState(initial = emptyList())
            val contact = contacts.find { it.id == contactId }
            
            AddContactScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                editContact = contact
            )
        }
        
        // Search
        composable(ContactRoutes.SEARCH) {
            ContactSearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onContactClick = { contact ->
                    navController.navigate(ContactRoutes.detail(contact.id))
                }
            )
        }
        
        // Duplicates
        composable(ContactRoutes.DUPLICATES) {
            DuplicatesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Helper composable to integrate Contacts into main app navigation
 */
@Composable
fun ContactsTab(
    viewModel: ContactViewModel,
    onSyncContacts: () -> Unit
) {
    val navController = rememberNavController()
    
    ContactNavigation(
        viewModel = viewModel,
        navController = navController,
        onSyncContacts = onSyncContacts
    )
}
