package com.smsforw.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import com.smsforw.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsForwarder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    sealed class SendResult {
        object Success : SendResult()
        data class Error(val message: String) : SendResult()
    }

    fun send(to: String, message: String): SendResult {
        return try {
            val smsManager = getSmsManager()

            if (message.length > 160) {
                val parts = smsManager.divideMessage(message)
                val sentIntents = parts.mapIndexed { index, _ ->
                    createSentPendingIntent(index)
                }
                smsManager.sendMultipartTextMessage(
                    to,
                    null,
                    parts,
                    sentIntents,
                    null
                )
            } else {
                val sentIntent = createSentPendingIntent(0)
                smsManager.sendTextMessage(to, null, message, sentIntent, null)
            }

            SendResult.Success
        } catch (e: Exception) {
            SendResult.Error(e.message ?: "Unknown error")
        }
    }

    @Suppress("DEPRECATION")
    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
    }

    private fun createSentPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
