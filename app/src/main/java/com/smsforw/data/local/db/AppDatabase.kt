package com.smsforw.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smsforw.data.local.db.dao.ForwardingRuleDao
import com.smsforw.data.local.db.entity.ForwardingRuleEntity

@Database(
    entities = [ForwardingRuleEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forwardingRuleDao(): ForwardingRuleDao
}
