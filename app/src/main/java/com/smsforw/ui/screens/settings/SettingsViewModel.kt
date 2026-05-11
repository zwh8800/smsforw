package com.smsforw.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smsforw.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val smsPermissionGranted: Boolean = false,
    val batteryOptimizationIgnored: Boolean = false,
    val forwardedCount: Long = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.settings
        .map { settings ->
            SettingsUiState(
                smsPermissionGranted = hasSmsPermissions(),
                batteryOptimizationIgnored = isBatteryOptimizationIgnored(),
                forwardedCount = settings.forwardedCount
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsUiState()
        )

    fun resetForwardedCount() {
        viewModelScope.launch {
            settingsRepository.resetForwardedCount()
        }
    }

    private fun hasSmsPermissions(): Boolean {
        val receiveSms = context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED
        val readSms = context.checkSelfPermission(Manifest.permission.READ_SMS) ==
                PackageManager.PERMISSION_GRANTED
        val sendSms = context.checkSelfPermission(Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED
        return receiveSms && readSms && sendSms
    }

    @Suppress("DEPRECATION")
    private fun isBatteryOptimizationIgnored(): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
