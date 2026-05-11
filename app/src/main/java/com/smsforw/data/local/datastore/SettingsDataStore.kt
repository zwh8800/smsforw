package com.smsforw.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val masterToggleEnabled: Boolean = false,
    val forwardedCount: Long = 0,
    val lastForwardTimestamp: Long = 0L
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val MASTER_TOGGLE = booleanPreferencesKey("master_toggle")
        val FORWARDED_COUNT = longPreferencesKey("forwarded_count")
        val LAST_FORWARD_TIMESTAMP = longPreferencesKey("last_forward_timestamp")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            masterToggleEnabled = prefs[Keys.MASTER_TOGGLE] ?: false,
            forwardedCount = prefs[Keys.FORWARDED_COUNT] ?: 0,
            lastForwardTimestamp = prefs[Keys.LAST_FORWARD_TIMESTAMP] ?: 0L
        )
    }

    suspend fun setMasterToggle(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MASTER_TOGGLE] = enabled
        }
    }

    suspend fun incrementForwardedCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FORWARDED_COUNT] ?: 0L
            prefs[Keys.FORWARDED_COUNT] = current + 1
            prefs[Keys.LAST_FORWARD_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun resetForwardedCount() {
        context.dataStore.edit { prefs ->
            prefs[Keys.FORWARDED_COUNT] = 0
            prefs[Keys.LAST_FORWARD_TIMESTAMP] = 0L
        }
    }
}
