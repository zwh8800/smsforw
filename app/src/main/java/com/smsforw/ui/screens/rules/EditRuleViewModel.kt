package com.smsforw.ui.screens.rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smsforw.data.model.ForwardingRule
import com.smsforw.data.model.KeywordMode
import com.smsforw.data.repository.RulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditRuleUiState(
    val ruleId: Long = 0,
    val name: String = "",
    val targetNumber: String = "",
    val keywords: String = "",
    val senderFilter: String = "",
    val matchAll: Boolean = true,
    val keywordMode: KeywordMode = KeywordMode.OR
)

@HiltViewModel
class EditRuleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val rulesRepository: RulesRepository
) : ViewModel() {

    private val ruleId: Long = savedStateHandle.get<Long>("ruleId") ?: 0L

    private val _uiState = MutableStateFlow(EditRuleUiState(ruleId = ruleId))
    val uiState: StateFlow<EditRuleUiState> = _uiState.asStateFlow()

    init {
        if (ruleId > 0) {
            viewModelScope.launch {
                val rule = rulesRepository.getRuleById(ruleId)
                if (rule != null) {
                    _uiState.value = EditRuleUiState(
                        ruleId = rule.id,
                        name = rule.name,
                        targetNumber = rule.targetNumber,
                        keywords = rule.keywords,
                        senderFilter = rule.senderFilter,
                        matchAll = rule.matchAll,
                        keywordMode = rule.keywordMode
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onTargetNumberChange(number: String) {
        _uiState.value = _uiState.value.copy(targetNumber = number)
    }

    fun onKeywordsChange(keywords: String) {
        _uiState.value = _uiState.value.copy(keywords = keywords)
    }

    fun onSenderFilterChange(filter: String) {
        _uiState.value = _uiState.value.copy(senderFilter = filter)
    }

    fun onMatchAllChange(matchAll: Boolean) {
        _uiState.value = _uiState.value.copy(matchAll = matchAll)
    }

    fun onKeywordModeChange(mode: KeywordMode) {
        _uiState.value = _uiState.value.copy(keywordMode = mode)
    }

    fun save() {
        val state = _uiState.value
        val rule = ForwardingRule(
            id = if (state.ruleId > 0) state.ruleId else 0,
            name = state.name.trim(),
            targetNumber = state.targetNumber.trim(),
            keywords = state.keywords.trim(),
            senderFilter = state.senderFilter.trim(),
            matchAll = state.matchAll,
            keywordMode = state.keywordMode,
            isEnabled = true
        )

        viewModelScope.launch {
            if (rule.id > 0) {
                rulesRepository.updateRule(rule)
            } else {
                rulesRepository.insertRule(rule)
            }
        }
    }
}
