package com.debbiedoesit.debbieai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.debbiedoesit.debbieai.core.ui.theme.DebbieAITheme
import com.debbiedoesit.debbieai.navigation.DebbieBottomNavigation
import com.debbiedoesit.debbieai.navigation.DebbieNavigation
// Import ViewModels when implemented
// import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModel
// import com.debbiedoesit.debbieai.contacts.viewmodel.ContactViewModelFactory
// import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModel
// import com.debbiedoesit.debbieai.photos.viewmodel.PhotoViewModelFactory

class MainActivity : ComponentActivity() {

    // ViewModels (uncomment when implemented)
    // private lateinit var contactViewModel: ContactViewModel
    // private lateinit var photoViewModel: PhotoViewModel

    // Permission launcher for contacts
    private val requestContactsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            syncDeviceContacts()
        } else {
            Toast.makeText(
                this,
                "Contacts permission required to sync device contacts",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Permission launcher for photos/media
    private val requestMediaPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            importPhotos()
        } else {
            Toast.makeText(
                this,
                "Media permission required to import photos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModels (uncomment when factories are created)
        // contactViewModel = ViewModelProvider(
        //     this,
        //     ContactViewModelFactory(applicationContext)
        // )[ContactViewModel::class.java]
        //
        // photoViewModel = ViewModelProvider(
        //     this,
        //     PhotoViewModelFactory(applicationContext)
        // )[PhotoViewModel::class.java]

        setContent {
            DebbieAITheme {
                DebbieAIApp(
                    onSyncContacts = { requestContactsPermissionAndSync() },
                    onImportPhotos = { requestMediaPermissionAndImport() }
                )
            }
        }
    }

    // ========================================================================
    // CONTACTS PERMISSION & SYNC
    // ========================================================================

    private fun requestContactsPermissionAndSync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    syncDeviceContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    Toast.makeText(
                        this,
                        "Contacts permission needed to import your contacts",
                        Toast.LENGTH_LONG
                    ).show()
                    requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
                else -> {
                    requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        } else {
            syncDeviceContacts()
        }
    }

    private fun syncDeviceContacts() {
        // Uncomment when ContactViewModel is available
        // contactViewModel.syncDeviceContacts()
        Toast.makeText(this, "Syncing contacts...", Toast.LENGTH_SHORT).show()
    }

    // ========================================================================
    // PHOTOS PERMISSION & IMPORT
    // ========================================================================

    private fun requestMediaPermissionAndImport() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) 
                == PackageManager.PERMISSION_GRANTED -> {
                importPhotos()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    this,
                    "Photo access needed to import job site photos",
                    Toast.LENGTH_LONG
                ).show()
                requestMediaPermissionLauncher.launch(permission)
            }
            else -> {
                requestMediaPermissionLauncher.launch(permission)
            }
        }
    }

    private fun importPhotos() {
        // Uncomment when PhotoViewModel is available
        // photoViewModel.importFromGallery()
        Toast.makeText(this, "Importing photos...", Toast.LENGTH_SHORT).show()
    }
}

// ============================================================================
// MAIN APP COMPOSABLE
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebbieAIApp(
    onSyncContacts: () -> Unit,
    onImportPhotos: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            DebbieBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        DebbieNavigation(
            navController = navController,
            onSyncContacts = onSyncContacts,
            onImportPhotos = onImportPhotos,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// Extension to pass modifier to DebbieNavigation
@Composable
fun DebbieNavigation(
    navController: androidx.navigation.NavHostController,
    onSyncContacts: () -> Unit,
    onImportPhotos: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Box(modifier = modifier) {
        DebbieNavigation(
            navController = navController,
            onSyncContacts = onSyncContacts,
            onImportPhotos = onImportPhotos
        )
    }
}
