package com.recovery.back.presentation.screens.alarms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recovery.back.data.local.entity.AlarmConfigEntity

@Composable
fun AlarmsScreen(
    alarms: List<AlarmConfigEntity>,
    onToggleAlarm: (AlarmConfigEntity, Boolean) -> Unit,
    onAddAlarmClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAlarmClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recovery Routines",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (alarms.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No alarms configured. Add reminders for medications or routines.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(alarms) { alarm ->
                        AlarmCard(alarm = alarm, onToggle = { isChecked -> onToggleAlarm(alarm, isChecked) })
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmConfigEntity,
    onToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alarm.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Add specific text for Posture reminder if applicable
                if (alarm.label.contains("Move", ignoreCase = true) || alarm.isWorkdayOnly) {
                    Text(
                        text = "Stand up. Engage light core brace. Walk for 5 minutes. Avoid forward bending.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Time formatting mockup
                    Text(
                        text = "08:00 AM", 
                        style = MaterialTheme.typography.bodyLarge,
                        fontFeatureSettings = "tnum"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (alarm.isWorkdayOnly) {
                        Badge { Text("Weekdays") }
                    }
                }
                
                // Snooze Config Indicator
                Text(
                    text = "Snooze: 10 mins",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Switch(
                checked = alarm.enabled,
                onCheckedChange = { onToggle(it) }
            )
        }
    }
}
