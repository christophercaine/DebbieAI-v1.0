package com.debbiedoesit.antigravity

import android.app.Application
import android.util.Log
import com.debbiedoesit.antigravity.data.AntigravityDataStore
import com.debbiedoesit.antigravity.utils.NetworkMonitor
import com.debbiedoesit.antigravity.workers.SyncWorker
import androidx.work.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AntigravityApplication : Application() {

    lateinit var dataStore: AntigravityDataStore
    lateinit var networkMonitor: NetworkMonitor
    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        dataStore = AntigravityDataStore(this)
        networkMonitor = NetworkMonitor(this)

        // Run device detection at startup
        mainScope.launch {
            val tier = DeviceCapabilityDetector.detect(this@AntigravityApplication)
            dataStore.saveDeviceTier(tier)
            Log.d("Antigravity", "Initial Device Tier Detected: $tier")
        }

        scheduleSync()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Log.e("FATAL_APP", "!!!! GLOBAL CRASH !!!!", throwable)
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AntigravitySync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
