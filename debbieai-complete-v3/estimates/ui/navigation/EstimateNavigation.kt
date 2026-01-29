package com.debbiedoesit.debbieai.estimates.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debbiedoesit.debbieai.estimates.ui.screens.*
import com.debbiedoesit.debbieai.estimates.viewmodel.EstimateViewModel

object EstimateRoutes {
    const val LIST = "estimates"
    const val DETAIL = "estimates/{estimateId}"
    const val CREATE = "estimates/create"
    const val CREATE_FOR_JOB = "estimates/create/job/{jobId}"
    const val CREATE_FOR_CONTACT = "estimates/create/contact/{contactId}"
    const val ADD_LINE_ITEM = "estimates/{estimateId}/add-item"
    
    fun detail(id: Long) = "estimates/$id"
    fun createForJob(jobId: Long) = "estimates/create/job/$jobId"
    fun createForContact(contactId: Long) = "estimates/create/contact/$contactId"
    fun addLineItem(estimateId: Long) = "estimates/$estimateId/add-item"
}

@Composable
fun EstimateNavigation(viewModel: EstimateViewModel, navController: NavHostController = rememberNavController(), onJobClick: (Long) -> Unit = {}, onContactClick: (Long) -> Unit = {}) {
    NavHost(navController = navController, startDestination = EstimateRoutes.LIST) {
        composable(EstimateRoutes.LIST) {
            EstimateListScreen(viewModel = viewModel, onEstimateClick = { navController.navigate(EstimateRoutes.detail(it)) }, onCreateClick = { navController.navigate(EstimateRoutes.CREATE) })
        }
        
        composable(EstimateRoutes.DETAIL, arguments = listOf(navArgument("estimateId") { type = NavType.LongType })) { backStackEntry ->
            val estimateId = backStackEntry.arguments?.getLong("estimateId") ?: 0L
            EstimateDetailScreen(estimateId = estimateId, viewModel = viewModel, onBackClick = { navController.popBackStack() }, onEditClick = { }, onAddLineItem = { navController.navigate(EstimateRoutes.addLineItem(it)) })
        }
        
        composable(EstimateRoutes.CREATE) {
            CreateEstimateScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }, onCreated = { navController.navigate(EstimateRoutes.detail(it)) { popUpTo(EstimateRoutes.CREATE) { inclusive = true } } })
        }
        
        composable(EstimateRoutes.CREATE_FOR_JOB, arguments = listOf(navArgument("jobId") { type = NavType.LongType })) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId")
            CreateEstimateScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }, onCreated = { navController.navigate(EstimateRoutes.detail(it)) { popUpTo(EstimateRoutes.CREATE_FOR_JOB) { inclusive = true } } }, jobId = jobId)
        }
        
        composable(EstimateRoutes.CREATE_FOR_CONTACT, arguments = listOf(navArgument("contactId") { type = NavType.LongType })) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId")
            CreateEstimateScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }, onCreated = { navController.navigate(EstimateRoutes.detail(it)) { popUpTo(EstimateRoutes.CREATE_FOR_CONTACT) { inclusive = true } } }, contactId = contactId)
        }
        
        composable(EstimateRoutes.ADD_LINE_ITEM, arguments = listOf(navArgument("estimateId") { type = NavType.LongType })) { backStackEntry ->
            val estimateId = backStackEntry.arguments?.getLong("estimateId") ?: 0L
            AddLineItemScreen(estimateId = estimateId, viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
    }
}

@Composable
fun EstimatesTab(viewModel: EstimateViewModel, onJobClick: (Long) -> Unit = {}, onContactClick: (Long) -> Unit = {}) {
    EstimateNavigation(viewModel = viewModel, onJobClick = onJobClick, onContactClick = onContactClick)
}