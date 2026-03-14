package com.recovery.back.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recovery.back.presentation.ui.theme.StreakFlame
import com.recovery.back.presentation.ui.theme.XpGold

@Composable
fun DashboardScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    streakDays: Int = 5,
    xp: Int = 1250,
    level: Int = 3,
    completionPercentage: Float = 0.6f, // 60%
    isRestDay: Boolean = false,
    onLogPainClick: () -> Unit,
    onRestDayToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Gamification Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Phase $currentPhase • Week $weekNumber",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lvl $level | XP: $xp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = XpGold
                )
            }

            // Streak Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Streak",
                    tint = StreakFlame
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$streakDays Days",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Daily Completion Ring with Lottie placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = completionPercentage,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 16.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(completionPercentage * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Complete", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Log Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(title = "Log Pain", icon = Icons.Default.Add, onClick = onLogPainClick)
            QuickActionButton(title = "Add Water", icon = Icons.Default.Add, onClick = { /* TODO */ })
            QuickActionButton(title = "Log Weight", icon = Icons.Default.Add, onClick = { /* TODO */ })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 20/5 Rest Day Toggle
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Rest Day Mode", fontWeight = FontWeight.Bold)
                    Text("Pause posture reminders", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = isRestDay, onCheckedChange = onRestDayToggle)
            }
        }
    }
}

@Composable
fun QuickActionButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(64.dp),
            shape = CircleShape
        ) {
            Icon(imageVector = icon, contentDescription = title)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelMedium)
    }
}
