package com.smsforw.ui.screens.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smsforw.data.model.ForwardingRule
import com.smsforw.data.repository.RulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val rulesRepository: RulesRepository
) : ViewModel() {

    val rules: StateFlow<List<ForwardingRule>> = rulesRepository.allRules
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun toggleRule(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            rulesRepository.setRuleEnabled(id, enabled)
        }
    }

    fun deleteRule(id: Long) {
        viewModelScope.launch {
            rulesRepository.deleteRule(id)
        }
    }
}
