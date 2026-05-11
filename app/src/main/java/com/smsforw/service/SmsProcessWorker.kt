package com.smsforw.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smsforw.data.repository.RulesRepository
import com.smsforw.data.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SmsProcessWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val rulesRepository: RulesRepository,
    private val settingsRepository: SettingsRepository,
    private val smsForwarder: SmsForwarder
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val sender = inputData.getString(KEY_SENDER) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val hash = inputData.getString(KEY_HASH) ?: return Result.failure()

        val settings = settingsRepository.settings.first()
        if (!settings.masterToggleEnabled) return Result.success()

        if (Deduplication.isDuplicate(hash)) return Result.success()
        Deduplication.record(hash)

        if (body.startsWith("[FW] ")) return Result.success()

        val enabledRules = rulesRepository.getEnabledRules()

        var forwarded = 0
        for (rule in enabledRules) {
            if (ruleMatches(rule, sender, body)) {
                val forwardBody = "[FW] 来自${sender}: ${body}"
                val result = smsForwarder.send(rule.targetNumber, forwardBody)
                if (result is SmsForwarder.SendResult.Success) {
                    forwarded++
                }
            }
        }

        if (forwarded > 0) {
            repeat(forwarded) { settingsRepository.incrementForwardedCount() }
        }

        return Result.success()
    }

    private fun ruleMatches(
        rule: com.smsforw.data.model.ForwardingRule,
        sender: String,
        body: String
    ): Boolean {
        val senderOk = rule.senderFilter.isBlank() ||
                sender.contains(rule.senderFilter, ignoreCase = true) ||
                sender.digitsOnly().contains(rule.senderFilter.digitsOnly())

        if (!senderOk) return false

        if (rule.matchAll) return true

        if (rule.keywords.isBlank()) return true

        val keywordList = rule.keywords.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (keywordList.isEmpty()) return true

        return when (rule.keywordMode) {
            com.smsforw.data.model.KeywordMode.AND ->
                keywordList.all { kw -> body.contains(kw, ignoreCase = true) }
            com.smsforw.data.model.KeywordMode.OR ->
                keywordList.any { kw -> body.contains(kw, ignoreCase = true) }
        }
    }

    private fun String.digitsOnly(): String = replace(Regex("[^0-9+]"), "")

    companion object {
        const val KEY_SENDER = "sender"
        const val KEY_BODY = "body"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_HASH = "hash"
    }
}
