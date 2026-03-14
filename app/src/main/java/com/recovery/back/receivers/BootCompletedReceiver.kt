package com.recovery.back.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.recovery.back.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val alarms = db.appDao().getEnabledAlarmsSync()
                    
                    // Logic to loop through alarms and reschedule them using AlarmManager
                    // Not writing the full AlarmManager boilerplate here, but this is the hook
                    alarms.forEach { alarm ->
                        if (alarm.enabled) {
                            // Scheduling logic will use alarm.nextTriggerTime and AlarmManager
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
