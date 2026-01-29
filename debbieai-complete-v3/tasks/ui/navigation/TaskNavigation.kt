package com.debbiedoesit.debbieai.tasks.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debbiedoesit.debbieai.tasks.ui.screens.*
import com.debbiedoesit.debbieai.tasks.viewmodel.TaskViewModel

/**
 * Navigation routes for Tasks module
 */
object TaskRoutes {
    const val LIST = "tasks/list"
    const val DETAIL = "tasks/detail/{taskId}"
    const val CREATE = "tasks/create"
    const val CREATE_FOR_CONTACT = "tasks/create/contact/{contactId}"
    const val CREATE_FOR_JOB = "tasks/create/job/{jobId}"
    const val EDIT = "tasks/edit/{taskId}"
    const val SEARCH = "tasks/search"
    const val TODAY = "tasks/today"
    const val CALENDAR = "tasks/calendar"
    
    fun detail(taskId: Long) = "tasks/detail/$taskId"
    fun createForContact(contactId: Long) = "tasks/create/contact/$contactId"
    fun createForJob(jobId: Long) = "tasks/create/job/$jobId"
    fun edit(taskId: Long) = "tasks/edit/$taskId"
}

/**
 * Task navigation graph
 */
@Composable
fun TaskNavigation(
    viewModel: TaskViewModel,
    navController: NavHostController = rememberNavController(),
    onContactClick: ((Long) -> Unit)? = null,
    onJobClick: ((Long) -> Unit)? = null,
    startDestination: String = TaskRoutes.LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Task List
        composable(TaskRoutes.LIST) {
            TaskListScreen(
                viewModel = viewModel,
                onTaskClick = { task ->
                    navController.navigate(TaskRoutes.detail(task.id))
                },
                onAddClick = {
                    navController.navigate(TaskRoutes.CREATE)
                },
                onSearchClick = {
                    navController.navigate(TaskRoutes.SEARCH)
                }
            )
        }
        
        // Task Detail
        composable(
            route = TaskRoutes.DETAIL,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
            val task = allTasks.find { it.id == taskId }
            
            if (task != null) {
                TaskDetailScreen(
                    task = task,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onContactClick = onContactClick,
                    onJobClick = onJobClick
                )
            }
        }
        
        // Create Task
        composable(TaskRoutes.CREATE) {
            CreateTaskScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Create Task for Contact
        composable(
            route = TaskRoutes.CREATE_FOR_CONTACT,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId")
            CreateTaskScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                contactId = contactId
            )
        }
        
        // Create Task for Job
        composable(
            route = TaskRoutes.CREATE_FOR_JOB,
            arguments = listOf(
                navArgument("jobId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId")
            CreateTaskScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                jobId = jobId
            )
        }
        
        // Edit Task
        composable(
            route = TaskRoutes.EDIT,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
            val task = allTasks.find { it.id == taskId }
            
            if (task != null) {
                EditTaskScreen(
                    task = task,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        
        // Search Tasks
        composable(TaskRoutes.SEARCH) {
            TaskSearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onTaskClick = { task ->
                    navController.navigate(TaskRoutes.detail(task.id))
                }
            )
        }
    }
}

/**
 * Helper composable for integrating Tasks into main navigation
 */
@Composable
fun TasksTab(
    viewModel: TaskViewModel,
    onContactClick: ((Long) -> Unit)? = null,
    onJobClick: ((Long) -> Unit)? = null
) {
    TaskNavigation(
        viewModel = viewModel,
        onContactClick = onContactClick,
        onJobClick = onJobClick
    )
}
