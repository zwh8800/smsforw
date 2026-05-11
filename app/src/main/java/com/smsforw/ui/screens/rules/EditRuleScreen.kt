package com.smsforw.ui.screens.rules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smsforw.data.model.ForwardingRule
import com.smsforw.data.model.KeywordMode

@Composable
fun EditRuleRoute(
    onBack: () -> Unit,
    viewModel: EditRuleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditRuleScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onTargetNumberChange = viewModel::onTargetNumberChange,
        onKeywordsChange = viewModel::onKeywordsChange,
        onSenderFilterChange = viewModel::onSenderFilterChange,
        onMatchAllChange = viewModel::onMatchAllChange,
        onKeywordModeChange = viewModel::onKeywordModeChange,
        onSave = {
            viewModel.save()
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRuleScreen(
    uiState: EditRuleUiState,
    onNameChange: (String) -> Unit,
    onTargetNumberChange: (String) -> Unit,
    onKeywordsChange: (String) -> Unit,
    onSenderFilterChange: (String) -> Unit,
    onMatchAllChange: (Boolean) -> Unit,
    onKeywordModeChange: (KeywordMode) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val isEdit = uiState.ruleId > 0
    val canSave = uiState.name.isNotBlank() && uiState.targetNumber.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "编辑规则" else "添加规则") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = canSave
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("规则名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.targetNumber,
                onValueChange = onTargetNumberChange,
                label = { Text("目标号码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "匹配所有短信",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = uiState.matchAll,
                    onCheckedChange = onMatchAllChange
                )
            }

            AnimatedVisibility(visible = !uiState.matchAll) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uiState.keywords,
                        onValueChange = onKeywordsChange,
                        label = { Text("关键词") },
                        placeholder = { Text("逗号分隔，留空则匹配所有短信") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.keywordMode == KeywordMode.OR,
                            onClick = { onKeywordModeChange(KeywordMode.OR) },
                            label = { Text("任一关键词匹配") }
                        )
                        FilterChip(
                            selected = uiState.keywordMode == KeywordMode.AND,
                            onClick = { onKeywordModeChange(KeywordMode.AND) },
                            label = { Text("所有关键词都匹配") }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.senderFilter,
                onValueChange = onSenderFilterChange,
                label = { Text("发件人过滤") },
                placeholder = { Text("号码或部分号码，留空则不限制") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
