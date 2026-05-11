package com.smsforw.data.repository

import com.smsforw.data.local.datastore.AppSettings
import com.smsforw.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    val settings: Flow<AppSettings> = settingsDataStore.settings

    suspend fun setMasterToggle(enabled: Boolean) =
        settingsDataStore.setMasterToggle(enabled)

    suspend fun incrementForwardedCount() =
        settingsDataStore.incrementForwardedCount()

    suspend fun resetForwardedCount() =
        settingsDataStore.resetForwardedCount()
}
