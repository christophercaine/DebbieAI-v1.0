package com.debbiedoesit.debbieai.jobs.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debbiedoesit.debbieai.jobs.ui.screens.*
import com.debbiedoesit.debbieai.jobs.viewmodel.JobViewModel

/**
 * Route definitions for Jobs module
 */
object JobRoutes {
    const val LIST = "jobs/list"
    const val DETAIL = "jobs/detail/{jobId}"
    const val CREATE = "jobs/create"
    const val CREATE_FOR_CONTACT = "jobs/create/{contactId}"
    const val EDIT = "jobs/edit/{jobId}"
    const val SEARCH = "jobs/search"
    
    // Helper functions
    fun detail(jobId: Long) = "jobs/detail/$jobId"
    fun edit(jobId: Long) = "jobs/edit/$jobId"
    fun createForContact(contactId: Long) = "jobs/create/$contactId"
}

/**
 * Jobs navigation graph - can be used standalone or nested
 */
@Composable
fun JobNavigation(
    viewModel: JobViewModel,
    navController: NavHostController = rememberNavController(),
    onContactClick: (Long) -> Unit = {},
    onPhotoClick: (Long) -> Unit = {},
    onEstimateClick: (Long) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = JobRoutes.LIST
    ) {
        // Job List
        composable(JobRoutes.LIST) {
            JobListScreen(
                viewModel = viewModel,
                onJobClick = { jobId ->
                    navController.navigate(JobRoutes.detail(jobId))
                },
                onCreateJob = {
                    navController.navigate(JobRoutes.CREATE)
                }
            )
        }
        
        // Job Detail
        composable(
            route = JobRoutes.DETAIL,
            arguments = listOf(
                navArgument("jobId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId") ?: 0L
            
            JobDetailScreen(
                jobId = jobId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(JobRoutes.edit(jobId)) },
                onContactClick = onContactClick,
                onPhotoClick = onPhotoClick,
                onEstimateClick = onEstimateClick
            )
        }
        
        // Create Job
        composable(JobRoutes.CREATE) {
            CreateJobScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onJobCreated = { jobId ->
                    navController.popBackStack()
                    navController.navigate(JobRoutes.detail(jobId))
                }
            )
        }
        
        // Create Job for Contact
        composable(
            route = JobRoutes.CREATE_FOR_CONTACT,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId")
            
            CreateJobScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onJobCreated = { jobId ->
                    navController.popBackStack()
                    navController.navigate(JobRoutes.detail(jobId))
                },
                preselectedContactId = contactId
            )
        }
        
        // Edit Job
        composable(
            route = JobRoutes.EDIT,
            arguments = listOf(
                navArgument("jobId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId") ?: 0L
            
            EditJobScreen(
                jobId = jobId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Helper composable for integrating Jobs into main app navigation
 */
@Composable
fun JobsTab(
    viewModel: JobViewModel,
    onNavigateToContact: (Long) -> Unit,
    onNavigateToPhoto: (Long) -> Unit
) {
    val navController = rememberNavController()
    
    JobNavigation(
        viewModel = viewModel,
        navController = navController,
        onContactClick = onNavigateToContact,
        onPhotoClick = onNavigateToPhoto
    )
}
