package com.smsforw.data.repository

import com.smsforw.data.local.db.dao.ForwardingRuleDao
import com.smsforw.data.local.db.entity.ForwardingRuleEntity
import com.smsforw.data.model.ForwardingRule
import com.smsforw.data.model.KeywordMode
import com.smsforw.data.model.toDomain
import com.smsforw.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RulesRepository @Inject constructor(
    private val ruleDao: ForwardingRuleDao
) {
    val allRules: Flow<List<ForwardingRule>> =
        ruleDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    val enabledRuleCount: Flow<Int> = ruleDao.enabledRuleCount()

    suspend fun getEnabledRules(): List<ForwardingRule> =
        ruleDao.getEnabledRules().map { it.toDomain() }

    suspend fun getRuleById(id: Long): ForwardingRule? =
        ruleDao.getById(id)?.toDomain()

    suspend fun insertRule(rule: ForwardingRule): Long =
        ruleDao.insert(rule.toEntity())

    suspend fun updateRule(rule: ForwardingRule) =
        ruleDao.update(rule.toEntity())

    suspend fun deleteRule(id: Long) =
        ruleDao.deleteById(id)

    suspend fun setRuleEnabled(id: Long, enabled: Boolean) =
        ruleDao.setEnabled(id, enabled)
}
