package com.debbiedoesdetails.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.debbiedoesdetails.app.data.local.ContactDatabase
import com.debbiedoesdetails.app.data.remote.RetrofitClient
import com.debbiedoesdetails.app.data.repository.ContactRepository
import com.debbiedoesdetails.app.data.sync.ContactSyncService
import com.debbiedoesdetails.app.ui.theme.DiddTheme
import com.debbiedoesdetails.app.viewmodel.ContactViewModel
import com.debbiedoesdetails.app.viewmodel.ContactViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var syncService: ContactSyncService
    private lateinit var viewModel: ContactViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            syncDeviceContacts()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ContactDatabase.getInstance(applicationContext)
        val apiService = RetrofitClient.getApiService()
        val repository = ContactRepository(database.contactDao(), apiService)
        syncService = ContactSyncService(applicationContext, repository)

        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(repository)
        ).get(ContactViewModel::class.java)

        requestContactsPermission()

        setContent {
            DiddTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(viewModel = viewModel, onRefresh = { syncDeviceContacts() })
                }
            }
        }
    }

    private fun requestContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                syncDeviceContacts()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        } else {
            syncDeviceContacts()
        }
    }

    private fun syncDeviceContacts() {
        lifecycleScope.launch {
            syncService.syncDeviceContacts()
            viewModel.loadContacts()
        }
    }
}