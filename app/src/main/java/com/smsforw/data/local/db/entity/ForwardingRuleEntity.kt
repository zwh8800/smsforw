package com.smsforw.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forwarding_rules")
data class ForwardingRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetNumber: String,
    val keywords: String = "",           // comma-separated, empty = match all
    val keywordMode: String = "OR",      // "AND" or "OR"
    val matchAll: Boolean = true,        // if true, ignore keywords and match everything
    val senderFilter: String = "",       // partial number match, empty = no filter
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
