package com.debbiedoesit.debbieai.photos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debbiedoesit.debbieai.photos.ui.screens.*
import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel

/**
 * Photo module routes
 */
object PhotoRoutes {
    const val GALLERY = "photos"
    const val DETAIL = "photos/{photoId}"
    const val ALBUMS = "photos/albums"
    const val ALBUM_DETAIL = "photos/albums/{albumId}"
    const val SEARCH = "photos/search"
    const val FAVORITES = "photos/favorites"
    const val TRASH = "photos/trash"
    
    fun photoDetail(photoId: Long) = "photos/$photoId"
    fun albumDetail(albumId: Long) = "photos/albums/$albumId"
}

/**
 * Photo module navigation graph
 * Can be used standalone or nested in main app navigation
 */
@Composable
fun PhotoNavigation(
    viewModel: PhotoViewModel,
    navController: NavHostController = rememberNavController(),
    onImportClick: () -> Unit,
    onLinkContactClick: (Long) -> Unit = {},
    startDestination: String = PhotoRoutes.GALLERY
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Main gallery
        composable(PhotoRoutes.GALLERY) {
            PhotoGalleryScreen(
                viewModel = viewModel,
                onPhotoClick = { photo ->
                    navController.navigate(PhotoRoutes.photoDetail(photo.id))
                },
                onAlbumsClick = {
                    navController.navigate(PhotoRoutes.ALBUMS)
                },
                onSearchClick = {
                    navController.navigate(PhotoRoutes.SEARCH)
                },
                onImportClick = onImportClick,
                onFavoritesClick = {
                    navController.navigate(PhotoRoutes.FAVORITES)
                },
                onTrashClick = {
                    navController.navigate(PhotoRoutes.TRASH)
                }
            )
        }
        
        // Photo detail
        composable(
            route = PhotoRoutes.DETAIL,
            arguments = listOf(
                navArgument("photoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
            PhotoDetailScreen(
                photoId = photoId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onLinkContactClick = onLinkContactClick,
                onViewAlbumClick = { albumId ->
                    navController.navigate(PhotoRoutes.albumDetail(albumId))
                }
            )
        }
        
        // Album list
        composable(PhotoRoutes.ALBUMS) {
            AlbumListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onAlbumClick = { album ->
                    navController.navigate(PhotoRoutes.albumDetail(album.id))
                }
            )
        }
        
        // Album detail
        composable(
            route = PhotoRoutes.ALBUM_DETAIL,
            arguments = listOf(
                navArgument("albumId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: 0L
            AlbumDetailScreen(
                albumId = albumId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPhotoClick = { photo ->
                    navController.navigate(PhotoRoutes.photoDetail(photo.id))
                },
                onAddPhotosClick = {
                    // TODO: Open photo picker to add to album
                    onImportClick()
                }
            )
        }
        
        // Search
        composable(PhotoRoutes.SEARCH) {
            PhotoSearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPhotoClick = { photo ->
                    navController.navigate(PhotoRoutes.photoDetail(photo.id))
                }
            )
        }
        
        // Favorites
        composable(PhotoRoutes.FAVORITES) {
            FavoritesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPhotoClick = { photo ->
                    navController.navigate(PhotoRoutes.photoDetail(photo.id))
                }
            )
        }
        
        // Trash
        composable(PhotoRoutes.TRASH) {
            TrashScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPhotoClick = { photo ->
                    navController.navigate(PhotoRoutes.photoDetail(photo.id))
                }
            )
        }
    }
}

/**
 * Helper to integrate photos into main navigation
 * Use this when photos is a tab in bottom navigation
 */
@Composable
fun PhotosTab(
    viewModel: PhotoViewModel,
    mainNavController: NavHostController,
    onImportClick: () -> Unit
) {
    val photosNavController = rememberNavController()
    
    PhotoNavigation(
        viewModel = viewModel,
        navController = photosNavController,
        onImportClick = onImportClick,
        onLinkContactClick = { photoId ->
            // Navigate to contact picker in main nav
            mainNavController.navigate("contacts/picker?returnTo=photos/$photoId")
        }
    )
}
