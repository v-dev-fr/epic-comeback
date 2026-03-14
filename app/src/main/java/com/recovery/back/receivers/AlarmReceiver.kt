package com.recovery.back.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val title = intent.getStringExtra("ALARM_TITLE") ?: "Recovery Reminder"
        val message = intent.getStringExtra("ALARM_MESSAGE") ?: "Time for your scheduled routine."
        val soundEnabled = intent.getBooleanExtra("SOUND_ENABLED", true)
        val vibrationEnabled = intent.getBooleanExtra("VIBRATION_ENABLED", true)

        showNotification(context, alarmId, title, message, soundEnabled, vibrationEnabled)
        
        // Gamification / Haptic feedback intent could be fired here
    }

    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "back_recovery_alarms"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recovery Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alarms for your recovery routine"
                if (soundEnabled) {
                    val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    setSound(ringtoneUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
                }
                enableVibration(vibrationEnabled)
                if (vibrationEnabled) {
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Snooze Intent Setup
        val snoozeIntent = Intent(context, SnoozeBroadcastReceiver::class.java).apply {
            putExtra("ALARM_ID", id)
            putExtra("ALARM_TITLE", title)
            putExtra("ALARM_MESSAGE", message)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, id, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Main App Intent (Using a generic class name for now, will be MainActivity)
        val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val mainPendingIntent = PendingIntent.getActivity(
            context, id, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            // .setSmallIcon(R.mipmap.ic_launcher) // Placeholder, needs actual icon
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Support long text for '20/5 Rule'
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze 10 Min", snoozePendingIntent)

        notificationManager.notify(id, builder.build())
    }
}
