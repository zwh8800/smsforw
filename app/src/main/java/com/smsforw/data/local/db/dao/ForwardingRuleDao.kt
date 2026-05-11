package com.smsforw.data.local.db.dao

import androidx.room.*
import com.smsforw.data.local.db.entity.ForwardingRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForwardingRuleDao {

    @Query("SELECT * FROM forwarding_rules ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ForwardingRuleEntity>>

    @Query("SELECT * FROM forwarding_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<ForwardingRuleEntity>

    @Query("SELECT * FROM forwarding_rules WHERE id = :id")
    suspend fun getById(id: Long): ForwardingRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: ForwardingRuleEntity): Long

    @Update
    suspend fun update(rule: ForwardingRuleEntity)

    @Delete
    suspend fun delete(rule: ForwardingRuleEntity)

    @Query("DELETE FROM forwarding_rules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE forwarding_rules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM forwarding_rules WHERE isEnabled = 1")
    fun enabledRuleCount(): Flow<Int>
}
