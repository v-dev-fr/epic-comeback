import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.presentation.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun DashboardScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    streakDays: Int = 5,
    xp: Int = 1250,
    level: Int = 3,
    completionPercentage: Float = 0.6f,
    isRestDay: Boolean = false,
    onLogPainClick: () -> Unit,
    onAddWaterClick: () -> Unit,
    onLogWeightClick: () -> Unit,
    onRestDayToggle: (Boolean) -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = completionPercentage,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HeaderSection(currentPhase, weekNumber, streakDays)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            // Hero Completion Card
            HeroCompletionCard(animatedProgress, level, xp)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ActionRow(onLogPainClick, onAddWaterClick, onLogWeightClick)
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            // Status Card
            SettingsCard(isRestDay, onRestDayToggle)
        }
    }
}

@Composable
fun HeaderSection(phase: Int, week: Int, streak: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Phase $phase",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = ElectricBlue
            )
            Text(
                text = "Week $week • Training Day",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Streak Chip
        Surface(
            color = Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFlame, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$streak", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun HeroCompletionCard(progress: Float, level: Int, xp: Int) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp)),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(0.02f), Color.Transparent)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Daily Progress", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Lvl $level Warrior", color = XpGold, fontWeight = FontWeight.SemiBold)
                    Text(text = "$xp XP", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }

                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        color = ElectricBlue,
                        trackColor = Color.White.copy(0.05f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ActionRow(onLogPain: () -> Unit, onAddWater: () -> Unit, onLogWeight: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            label = "Log Pain",
            icon = Icons.Default.AddAlert,
            color = ElectricBlue,
            onClick = onLogPain,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Water",
            icon = Icons.Default.WaterDrop,
            color = Color(0xFF63B3ED),
            onClick = onAddWater,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Weight",
            icon = Icons.Default.MonitorWeight,
            color = NeonGreen,
            onClick = onLogWeight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        color = SurfaceDark,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(100.dp).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SettingsCard(isRestDay: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color.White.copy(0.6f))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Rest Day Mode", fontWeight = FontWeight.Bold)
                    Text("Pause posture pings", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Switch(
                checked = isRestDay,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonGreen,
                    checkedTrackColor = NeonGreen.copy(0.2f)
                )
            )
        }
    }
}
