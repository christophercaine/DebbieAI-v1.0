package com.debbiedoesit.debbieai.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// ============================================================================
// ROUTE DEFINITIONS
// ============================================================================

object DebbieRoutes {
    // Home / Dashboard
    const val HOME = "home"
    
    // Contacts Module
    const val CONTACT_LIST = "contacts"
    const val CONTACT_DETAIL = "contacts/{contactId}"
    const val ADD_CONTACT = "contacts/add"
    const val DUPLICATES = "contacts/duplicates"
    
    // Photos Module
    const val PHOTO_GALLERY = "photos"
    const val PHOTO_DETAIL = "photos/{photoId}"
    const val ALBUMS = "photos/albums"
    const val ALBUM_DETAIL = "photos/albums/{albumId}"
    const val PHOTO_SEARCH = "photos/search"
    const val FAVORITES = "photos/favorites"
    const val TRASH = "photos/trash"
    
    // Jobs Module
    const val JOB_LIST = "jobs"
    const val JOB_DETAIL = "jobs/{jobId}"
    const val JOB_DASHBOARD = "jobs/{jobId}/dashboard"
    const val ADD_JOB = "jobs/add"
    
    // Details Module (Tasks/Calendar)
    const val TASK_LIST = "tasks"
    const val TASK_DETAIL = "tasks/{taskId}"
    const val ADD_TASK = "tasks/add"
    const val CALENDAR = "calendar"
    const val DAILY_BRIEFING = "briefing"
    const val CREW = "crew"
    
    // Drafting Module (Estimates)
    const val ESTIMATE_LIST = "estimates"
    const val ESTIMATE_DETAIL = "estimates/{estimateId}"
    const val ESTIMATE_BUILDER = "estimates/builder"
    const val MATERIALS = "materials"
    const val TEMPLATES = "templates"
    
    // Data/AI Module
    const val INSIGHTS = "insights"
    const val ANALYSIS_QUEUE = "analysis"
    
    // Settings
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    
    // Helper functions to build routes with parameters
    fun contactDetail(contactId: Long) = "contacts/$contactId"
    fun photoDetail(photoId: Long) = "photos/$photoId"
    fun albumDetail(albumId: Long) = "photos/albums/$albumId"
    fun jobDetail(jobId: Long) = "jobs/$jobId"
    fun jobDashboard(jobId: Long) = "jobs/$jobId/dashboard"
    fun taskDetail(taskId: Long) = "tasks/$taskId"
    fun estimateDetail(estimateId: Long) = "estimates/$estimateId"
}

// ============================================================================
// BOTTOM NAVIGATION ITEMS
// ============================================================================

enum class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
) {
    HOME(
        route = DebbieRoutes.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "Home"
    ),
    JOBS(
        route = DebbieRoutes.JOB_LIST,
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.Work,
        label = "Jobs"
    ),
    PHOTOS(
        route = DebbieRoutes.PHOTO_GALLERY,
        selectedIcon = Icons.Filled.PhotoLibrary,
        unselectedIcon = Icons.Outlined.PhotoLibrary,
        label = "Photos"
    ),
    TASKS(
        route = DebbieRoutes.TASK_LIST,
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        label = "Tasks"
    ),
    MORE(
        route = "more",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        label = "More"
    );
    
    companion object {
        // Routes that should highlight the corresponding bottom nav item
        val jobRoutes = listOf(DebbieRoutes.JOB_LIST, "jobs/")
        val photoRoutes = listOf(DebbieRoutes.PHOTO_GALLERY, "photos/")
        val taskRoutes = listOf(DebbieRoutes.TASK_LIST, "tasks/", DebbieRoutes.CALENDAR)
        
        fun fromRoute(route: String?): BottomNavItem {
            if (route == null) return HOME
            return when {
                route == DebbieRoutes.HOME -> HOME
                jobRoutes.any { route.startsWith(it) } -> JOBS
                photoRoutes.any { route.startsWith(it) } -> PHOTOS
                taskRoutes.any { route.startsWith(it) } -> TASKS
                else -> MORE
            }
        }
    }
}

// ============================================================================
// MORE MENU ITEMS (accessed from "More" tab)
// ============================================================================

data class MoreMenuItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val description: String
)

val moreMenuItems = listOf(
    MoreMenuItem(
        route = DebbieRoutes.CONTACT_LIST,
        icon = Icons.Outlined.People,
        label = "Contacts",
        description = "Customer CRM"
    ),
    MoreMenuItem(
        route = DebbieRoutes.ESTIMATE_LIST,
        icon = Icons.Outlined.Description,
        label = "Estimates",
        description = "Quotes & proposals"
    ),
    MoreMenuItem(
        route = DebbieRoutes.MATERIALS,
        icon = Icons.Outlined.Inventory,
        label = "Materials",
        description = "Pricing database"
    ),
    MoreMenuItem(
        route = DebbieRoutes.INSIGHTS,
        icon = Icons.Outlined.Insights,
        label = "Insights",
        description = "Business analytics"
    ),
    MoreMenuItem(
        route = DebbieRoutes.CREW,
        icon = Icons.Outlined.Group,
        label = "Crew",
        description = "Team management"
    ),
    MoreMenuItem(
        route = DebbieRoutes.SETTINGS,
        icon = Icons.Outlined.Settings,
        label = "Settings",
        description = "App preferences"
    )
)

// ============================================================================
// BOTTOM NAVIGATION BAR
// ============================================================================

@Composable
fun DebbieBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedItem = BottomNavItem.fromRoute(currentRoute)
    
    NavigationBar(modifier = modifier) {
        BottomNavItem.entries.forEach { item ->
            val selected = item == selectedItem
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    if (item == BottomNavItem.MORE) {
                        // Handle "More" differently - could show bottom sheet or navigate
                        navController.navigate("more") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// ============================================================================
// MAIN NAVIGATION HOST
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebbieNavigation(
    navController: NavHostController = rememberNavController(),
    // ViewModels would be passed here
    onSyncContacts: () -> Unit = {},
    onImportPhotos: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = DebbieRoutes.HOME
    ) {
        // ====================================================================
        // HOME / DASHBOARD
        // ====================================================================
        composable(DebbieRoutes.HOME) {
            // TODO: HomeDashboardScreen
            PlaceholderScreen(
                title = "DebbieAI Dashboard",
                description = "Your contractor command center",
                navController = navController
            )
        }
        
        // ====================================================================
        // CONTACTS MODULE
        // ====================================================================
        composable(DebbieRoutes.CONTACT_LIST) {
            // TODO: ContactListScreen(viewModel, navController, onSyncContacts)
            PlaceholderScreen(
                title = "Contacts",
                description = "Customer CRM",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.CONTACT_DETAIL,
            arguments = listOf(navArgument("contactId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0L
            // TODO: ContactDetailScreen(contactId, viewModel, navController)
            PlaceholderScreen(
                title = "Contact Detail",
                description = "Contact ID: $contactId",
                navController = navController
            )
        }
        
        composable(DebbieRoutes.ADD_CONTACT) {
            // TODO: AddContactScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Add Contact",
                description = "Create new contact",
                navController = navController
            )
        }
        
        composable(DebbieRoutes.DUPLICATES) {
            // TODO: DuplicatesScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Duplicates",
                description = "Review potential duplicates",
                navController = navController
            )
        }
        
        // ====================================================================
        // PHOTOS MODULE
        // ====================================================================
        composable(DebbieRoutes.PHOTO_GALLERY) {
            // TODO: PhotoGalleryScreen(viewModel, navController, onImportPhotos)
            PlaceholderScreen(
                title = "Photos",
                description = "Job site photo library",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.PHOTO_DETAIL,
            arguments = listOf(navArgument("photoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            // TODO: PhotoDetailScreen(photoId, viewModel, navController)
            PlaceholderScreen(
                title = "Photo Detail",
                description = "Photo ID: $photoId",
                navController = navController
            )
        }
        
        composable(DebbieRoutes.ALBUMS) {
            // TODO: AlbumListScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Albums",
                description = "Photo albums",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.ALBUM_DETAIL,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: 0L
            // TODO: AlbumDetailScreen(albumId, viewModel, navController)
            PlaceholderScreen(
                title = "Album Detail",
                description = "Album ID: $albumId",
                navController = navController
            )
        }
        
        composable(DebbieRoutes.PHOTO_SEARCH) {
            PlaceholderScreen("Photo Search", "Search photos", navController)
        }
        
        composable(DebbieRoutes.FAVORITES) {
            PlaceholderScreen("Favorites", "Favorite photos", navController)
        }
        
        composable(DebbieRoutes.TRASH) {
            PlaceholderScreen("Trash", "Deleted photos", navController)
        }
        
        // ====================================================================
        // JOBS MODULE
        // ====================================================================
        composable(DebbieRoutes.JOB_LIST) {
            // TODO: JobListScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Jobs",
                description = "Active projects",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.JOB_DETAIL,
            arguments = listOf(navArgument("jobId") { type = NavType.LongType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId") ?: 0L
            // TODO: JobDetailScreen(jobId, viewModel, navController)
            PlaceholderScreen(
                title = "Job Detail",
                description = "Job ID: $jobId",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.JOB_DASHBOARD,
            arguments = listOf(navArgument("jobId") { type = NavType.LongType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getLong("jobId") ?: 0L
            PlaceholderScreen("Job Dashboard", "Job $jobId overview", navController)
        }
        
        composable(DebbieRoutes.ADD_JOB) {
            PlaceholderScreen("Add Job", "Create new job", navController)
        }
        
        // ====================================================================
        // DETAILS MODULE (Tasks/Calendar)
        // ====================================================================
        composable(DebbieRoutes.TASK_LIST) {
            // TODO: TaskListScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Tasks",
                description = "To-do list",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            PlaceholderScreen("Task Detail", "Task ID: $taskId", navController)
        }
        
        composable(DebbieRoutes.ADD_TASK) {
            PlaceholderScreen("Add Task", "Create new task", navController)
        }
        
        composable(DebbieRoutes.CALENDAR) {
            PlaceholderScreen("Calendar", "Schedule view", navController)
        }
        
        composable(DebbieRoutes.DAILY_BRIEFING) {
            PlaceholderScreen("Daily Briefing", "Today's overview", navController)
        }
        
        composable(DebbieRoutes.CREW) {
            PlaceholderScreen("Crew", "Team members", navController)
        }
        
        // ====================================================================
        // DRAFTING MODULE (Estimates)
        // ====================================================================
        composable(DebbieRoutes.ESTIMATE_LIST) {
            // TODO: EstimateListScreen(viewModel, navController)
            PlaceholderScreen(
                title = "Estimates",
                description = "Quotes & proposals",
                navController = navController
            )
        }
        
        composable(
            route = DebbieRoutes.ESTIMATE_DETAIL,
            arguments = listOf(navArgument("estimateId") { type = NavType.LongType })
        ) { backStackEntry ->
            val estimateId = backStackEntry.arguments?.getLong("estimateId") ?: 0L
            PlaceholderScreen("Estimate Detail", "Estimate #$estimateId", navController)
        }
        
        composable(DebbieRoutes.ESTIMATE_BUILDER) {
            PlaceholderScreen("Estimate Builder", "Create estimate", navController)
        }
        
        composable(DebbieRoutes.MATERIALS) {
            PlaceholderScreen("Materials", "Pricing database", navController)
        }
        
        composable(DebbieRoutes.TEMPLATES) {
            PlaceholderScreen("Templates", "Estimate templates", navController)
        }
        
        // ====================================================================
        // DATA/AI MODULE
        // ====================================================================
        composable(DebbieRoutes.INSIGHTS) {
            PlaceholderScreen("Insights", "Business analytics", navController)
        }
        
        composable(DebbieRoutes.ANALYSIS_QUEUE) {
            PlaceholderScreen("Analysis Queue", "Pending AI analysis", navController)
        }
        
        // ====================================================================
        // SETTINGS
        // ====================================================================
        composable(DebbieRoutes.SETTINGS) {
            PlaceholderScreen("Settings", "App preferences", navController)
        }
        
        composable(DebbieRoutes.PROFILE) {
            PlaceholderScreen("Profile", "Your account", navController)
        }
        
        // ====================================================================
        // MORE MENU
        // ====================================================================
        composable("more") {
            MoreMenuScreen(navController = navController)
        }
    }
}

// ============================================================================
// MORE MENU SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            moreMenuItems.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.label) },
                    supportingContent = { Text(item.description) },
                    leadingContent = {
                        Icon(item.icon, contentDescription = item.label)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ListItemDefaults.colors()
                )
                HorizontalDivider()
            }
        }
    }
}

// ============================================================================
// PLACEHOLDER SCREEN (for unimplemented screens)
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(androidx.compose.ui.unit.dp.times(16)))
                Text(
                    text = "🚧 Coming Soon",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// ============================================================================
// NAVIGATION HELPERS
// ============================================================================

fun NavController.navigateToContact(contactId: Long) {
    navigate(DebbieRoutes.contactDetail(contactId))
}

fun NavController.navigateToPhoto(photoId: Long) {
    navigate(DebbieRoutes.photoDetail(photoId))
}

fun NavController.navigateToAlbum(albumId: Long) {
    navigate(DebbieRoutes.albumDetail(albumId))
}

fun NavController.navigateToJob(jobId: Long) {
    navigate(DebbieRoutes.jobDetail(jobId))
}

fun NavController.navigateToTask(taskId: Long) {
    navigate(DebbieRoutes.taskDetail(taskId))
}

fun NavController.navigateToEstimate(estimateId: Long) {
    navigate(DebbieRoutes.estimateDetail(estimateId))
}
