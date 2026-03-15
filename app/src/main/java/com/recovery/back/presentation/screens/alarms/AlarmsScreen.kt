package com.recovery.back.presentation.screens.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.data.local.entity.AlarmConfigEntity
import com.recovery.back.presentation.ui.theme.*

@Composable
fun AlarmsScreen(
    alarms: List<AlarmConfigEntity> = emptyList(),
    onToggleAlarm: (AlarmConfigEntity, Boolean) -> Unit = { _, _ -> },
    onAddAlarmClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Obsidian,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlarmClick,
                containerColor = ElectricBlue,
                contentColor = Obsidian,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Routine Alerts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Posture & medication reminders",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (alarms.isEmpty()) {
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                        Text(
                            "No active alerts. Tap + to set your recovery window.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(alarms) { alarm ->
                        AlarmCard(alarm = alarm, onToggle = { isChecked -> onToggleAlarm(alarm, isChecked) })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Helpful Tip Card
            Surface(
                color = NeonGreen.copy(0.05f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, NeonGreen.copy(0.1f), RoundedCornerShape(20.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "The 20/5 Rule: Move every 20 mins for 5 mins to maintain disc hydration.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        lineHeight = 16.sp
                    )
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
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.NotificationsActive, 
                        contentDescription = null, 
                        tint = if(alarm.enabled) ElectricBlue else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = alarm.label,
                        fontWeight = FontWeight.Bold,
                        color = if(alarm.enabled) TextPrimary else TextSecondary
                    )
                    Text(
                        text = if(alarm.isWorkdayOnly) "Weekdays only" else "Every day",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    if (alarm.enabled) {
                        Text(
                            text = "08:00 AM", // Mocked for UI
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricBlue,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Switch(
                checked = alarm.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonGreen,
                    checkedTrackColor = NeonGreen.copy(0.2f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = Color.White.copy(0.1f)
                )
            )
        }
    }
}
