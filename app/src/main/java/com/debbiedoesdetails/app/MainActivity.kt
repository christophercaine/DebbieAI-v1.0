package com.debbiedoesdetails.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.debbiedoesdetails.app.ui.navigation.AppNavigation
import com.debbiedoesdetails.app.ui.theme.DiddTheme
import com.debbiedoesdetails.app.viewmodel.ContactViewModel
import com.debbiedoesdetails.app.viewmodel.ContactViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ContactViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.syncDeviceContacts()
        } else {
            Toast.makeText(
                this,
                "Contacts permission required to sync device contacts",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel using the factory (handles database, repository, sync service)
        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(applicationContext)
        )[ContactViewModel::class.java]

        // Set content
        setContent {
            DiddTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        onSyncContacts = { requestContactsPermissionAndSync() }
                    )
                }
            }
        }

        // Request permission and sync on first launch
        requestContactsPermissionAndSync()
    }

    private fun requestContactsPermissionAndSync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted, sync contacts
                    viewModel.syncDeviceContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    // Show explanation then request
                    Toast.makeText(
                        this,
                        "Contacts permission needed to import your contacts",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }
        } else {
            // No runtime permission needed for older Android
            viewModel.syncDeviceContacts()
        }
    }
}