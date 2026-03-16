package com.recovery.back.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.recovery.back.data.local.entity.AlarmConfigEntity
import com.recovery.back.receivers.AlarmReceiver

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: AlarmConfigEntity) {
        if (!alarm.enabled) {
            cancelAlarm(alarm.id)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_TITLE", alarm.label)
            putExtra("ALARM_MESSAGE", getMessageForLabel(alarm.label))
            putExtra("SOUND_ENABLED", alarm.soundEnabled)
            putExtra("VIBRATION_ENABLED", alarm.vibrationEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = if (alarm.nextTriggerTime > System.currentTimeMillis()) {
            alarm.nextTriggerTime
        } else {
            // If nextTriggerTime is not set or in the past, calculate it
            calculateInitialTrigger(alarm)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Scheduled alarm ${alarm.id} for $triggerTime")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing", e)
        }
    }

    fun cancelAlarm(id: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("AlarmScheduler", "Cancelled alarm $id")
        }
    }

    private fun calculateInitialTrigger(alarm: AlarmConfigEntity): Long {
        // Simple logic for repeating alarms vs one-time ones
        return if (alarm.repeatIntervalMillis > 0) {
            System.currentTimeMillis() + alarm.repeatIntervalMillis
        } else {
            // For one-time alarms, we'd normally parse baseTimeMillisOfDay
            // but for movement reminders we use intervals.
            System.currentTimeMillis() + 2400000 // Default 40 mins
        }
    }

    private fun getMessageForLabel(label: String): String {
        return when {
            label.contains("20/5") || label.contains("Posture") -> "Disc hydration time! Move for 5 minutes."
            label.contains("Water") -> "Time to hydrate! Grab a glass of water."
            else -> "Time for your scheduled routine."
        }
    }
}
