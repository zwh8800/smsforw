package com.smsforw.data.model

data class ForwardingRule(
    val id: Long = 0,
    val name: String,
    val targetNumber: String,
    val keywords: String = "",
    val keywordMode: KeywordMode = KeywordMode.OR,
    val matchAll: Boolean = true,
    val senderFilter: String = "",
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class KeywordMode {
    AND, OR;

    override fun toString(): String = name
}

fun ForwardingRule.toEntity() = com.smsforw.data.local.db.entity.ForwardingRuleEntity(
    id = id,
    name = name,
    targetNumber = targetNumber,
    keywords = keywords,
    keywordMode = keywordMode.name,
    matchAll = matchAll,
    senderFilter = senderFilter,
    isEnabled = isEnabled,
    createdAt = createdAt
)

fun com.smsforw.data.local.db.entity.ForwardingRuleEntity.toDomain() = ForwardingRule(
    id = id,
    name = name,
    targetNumber = targetNumber,
    keywords = keywords,
    keywordMode = KeywordMode.valueOf(keywordMode),
    matchAll = matchAll,
    senderFilter = senderFilter,
    isEnabled = isEnabled,
    createdAt = createdAt
)
