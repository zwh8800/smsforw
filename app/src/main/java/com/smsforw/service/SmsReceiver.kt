package com.smsforw.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.security.MessageDigest

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val pendingResult = goAsync()
        try {
            val sender = messages.first().originatingAddress ?: run {
                pendingResult.finish()
                return
            }
            val body = messages.joinToString("") { it.messageBody ?: "" }
            val timestamp = messages.first().timestampMillis

            val hash = Deduplication.hash(sender, body, timestamp)

            val inputData = Data.Builder()
                .putString(SmsProcessWorker.KEY_SENDER, sender)
                .putString(SmsProcessWorker.KEY_BODY, body)
                .putLong(SmsProcessWorker.KEY_TIMESTAMP, timestamp)
                .putString(SmsProcessWorker.KEY_HASH, hash)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SmsProcessWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        } finally {
            pendingResult.finish()
        }
    }
}

object Deduplication {

    private val recentHashes = android.util.LruCache<String, Long>(50)

    fun hash(sender: String, body: String, timestamp: Long): String {
        val timeBucket = timestamp / 60_000
        val input = "$sender|$timeBucket|$body"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun isDuplicate(hash: String): Boolean = recentHashes.get(hash) != null

    fun record(hash: String) {
        recentHashes.put(hash, System.currentTimeMillis())
    }
}
