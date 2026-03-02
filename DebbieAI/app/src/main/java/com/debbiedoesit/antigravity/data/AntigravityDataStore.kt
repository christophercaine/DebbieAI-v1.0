package com.debbiedoesit.antigravity.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.debbiedoesit.antigravity.DeviceTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "antigravity_prefs")

class AntigravityDataStore(private val context: Context) {

    private val DEVICE_TIER_KEY = stringPreferencesKey("device_tier")

    val deviceTierFlow: Flow<DeviceTier> = context.dataStore.data
        .map { preferences ->
            val tierName = preferences[DEVICE_TIER_KEY] ?: DeviceTier.LITE.name
            DeviceTier.valueOf(tierName)
        }

    suspend fun saveDeviceTier(tier: DeviceTier) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_TIER_KEY] = tier.name
        }
    }
}
