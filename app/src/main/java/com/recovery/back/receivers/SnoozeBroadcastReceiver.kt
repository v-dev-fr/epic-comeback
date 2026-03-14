package com.recovery.back.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SnoozeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val title = intent.getStringExtra("ALARM_TITLE")
        val message = intent.getStringExtra("ALARM_MESSAGE")

        if (alarmId != -1) {
            snoozeAlarm(context, alarmId, title, message)
        }
    }

    private fun snoozeAlarm(context: Context, id: Int, title: String?, message: String?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", id)
            putExtra("ALARM_TITLE", title)
            putExtra("ALARM_MESSAGE", message)
            putExtra("SOUND_ENABLED", true)
            putExtra("VIBRATION_ENABLED", true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze for 10 minutes
        val triggerTime = System.currentTimeMillis() + (10 * 60 * 1000)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Toast.makeText(context, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            // Missing exact alarm permission
            Toast.makeText(context, "Cannot snooze: Exact alarm permission missing", Toast.LENGTH_LONG).show()
        }
    }
}
