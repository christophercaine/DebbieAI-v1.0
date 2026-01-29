package com.debbiedoesdetails.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.debbiedoesdetails.app.ui.screens.AddContactScreen
import com.debbiedoesdetails.app.ui.screens.ContactDetailScreen
import com.debbiedoesdetails.app.ui.screens.ContactListScreen
import com.debbiedoesdetails.app.ui.screens.DuplicatesScreen
import com.debbiedoesdetails.app.viewmodel.ContactViewModel

@Composable
fun AppNavigation(viewModel: ContactViewModel, onRefresh: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "contact_list") {
        composable("contact_list") {
            ContactListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add_contact") },
                onContactClick = { contact ->
                    navController.navigate("contact_detail/${contact.id}")
                },
                onDuplicatesClick = { navController.navigate("duplicates") },
                onRefresh = onRefresh
            )
        }
        composable("add_contact") {
            AddContactScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("contact_detail/{contactId}") { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")?.toLongOrNull() ?: 0L
            val contacts by viewModel.contacts.collectAsState(initial = emptyList())
            val contact = contacts.find { it.id == contactId }
            if (contact != null) {
                ContactDetailScreen(
                    contact = contact,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        composable("duplicates") {
            DuplicatesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
