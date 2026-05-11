package com.smsforw.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smsforw.data.local.datastore.AppSettings
import com.smsforw.data.repository.RulesRepository
import com.smsforw.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val masterToggle: Boolean = false,
    val forwardedCount: Long = 0,
    val lastForwardTimestamp: Long = 0L,
    val enabledRuleCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val rulesRepository: RulesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = settingsRepository.settings
        .combine(rulesRepository.enabledRuleCount) { settings, ruleCount ->
            HomeUiState(
                masterToggle = settings.masterToggleEnabled,
                forwardedCount = settings.forwardedCount,
                lastForwardTimestamp = settings.lastForwardTimestamp,
                enabledRuleCount = ruleCount
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState()
        )

    fun setMasterToggle(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setMasterToggle(enabled)
        }
    }
}
