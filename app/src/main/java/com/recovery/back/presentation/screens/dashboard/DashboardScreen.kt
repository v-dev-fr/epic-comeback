package com.recovery.back.presentation.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.presentation.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    streakDays: Int = 5,
    xp: Int = 1250,
    level: Int = 3,
    completionPercentage: Float = 0.6f,
    isRestDay: Boolean = false,
    waterCount: Int = 0,
    onLogPainClick: (Int, Int) -> Unit,
    onAddWaterClick: () -> Unit,
    onLogWeightClick: (Float) -> Unit,
    onRestDayToggle: (Boolean) -> Unit
) {
    var showPainDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(xp) {
        if (xp > 0 && xp % 1000 == 0) {
            showCelebration = true
            delay(3000)
            showCelebration = false
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = completionPercentage,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    if (showPainDialog) {
        PainLogDialog(onDismiss = { showPainDialog = false }, onConfirm = { b, s -> 
            onLogPainClick(b, s)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showPainDialog = false 
        })
    }
    if (showWeightDialog) {
        WeightLogDialog(onDismiss = { showWeightDialog = false }, onConfirm = { w -> 
            onLogWeightClick(w)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showWeightDialog = false 
        })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Obsidian).padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                LevelHeader(level, xp, waterCount)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BentoGrid(currentPhase, streakDays, waterCount, animatedProgress, onAddWater = {
                    onAddWaterClick()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                })
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(text = "Consistency", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                ActivityHeatmap()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                QuickActionsRow(onLogPain = { showPainDialog = true }, onLogWeight = { showWeightDialog = true })
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                SettingsCard(isRestDay, onRestDayToggle)
            }
        }

        if (showCelebration) {
            ConfettiOverlay()
        }
    }
}

@Composable
fun LevelHeader(level: Int, xp: Int, water: Int) {
    val xpInCurrentLevel = xp % 1000
    val progress = xpInCurrentLevel / 1000f
    
    val greeting = when {
        water < 3 -> "Stay hydrated, Vivek!"
        water < 8 -> "Almost at your water goal!"
        else -> "Hydration master today!"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(text = "Lvl $level Warrior", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = greeting, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(text = "$xpInCurrentLevel / 1000 XP", style = MaterialTheme.typography.labelMedium, color = XpGold, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Color.White.copy(0.05f))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(CircleShape).background(Brush.horizontalGradient(listOf(ElectricBlue, NeonGreen))))
        }
    }
}

@Composable
fun BentoGrid(phase: Int, streak: Int, water: Int, progress: Float, onAddWater: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().height(180.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                modifier = Modifier.weight(1.5f).fillMaxHeight(),
                color = SurfaceDark,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                        CircularProgressIndicator(progress = progress, modifier = Modifier.fillMaxSize(), strokeWidth = 8.dp, color = ElectricBlue, trackColor = Color.White.copy(0.05f), strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
                        Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Daily Routine", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                }
            }

            Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(modifier = Modifier.weight(1f).fillMaxWidth(), color = SurfaceDark, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color.White.copy(0.05f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFlame, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "$streak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Days", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
                Surface(modifier = Modifier.weight(1f).fillMaxWidth(), color = ElectricBlue.copy(0.1f), shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, ElectricBlue.copy(0.2f))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(text = "Phase", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                        Text(text = "$phase", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ElectricBlue)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            color = SurfaceDark,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(0.05f)),
            onClick = onAddWater
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF63B3ED).copy(0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF63B3ED))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Hydration", fontWeight = FontWeight.Bold)
                        Text(text = "$water / 8 glasses today", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF63B3ED))
            }
        }
    }
}

@Composable
fun ActivityHeatmap() {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(20) { col ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(7) { row ->
                            val active = (col + row) % 3 == 0
                            val alpha = if (active) (0.2f + (col % 5) * 0.15f).coerceAtMost(1f) else 0.05f
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(NeonGreen.copy(alpha = alpha)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(onLogPain: () -> Unit, onLogWeight: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(modifier = Modifier.weight(1f).height(64.dp), color = SurfaceDark, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color.White.copy(0.05f)), onClick = onLogPain) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Default.AddAlert, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log Pain", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        Surface(modifier = Modifier.weight(1f).height(64.dp), color = SurfaceDark, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color.White.copy(0.05f)), onClick = onLogWeight) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Weight", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
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
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.05f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color.White.copy(0.6f))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Rest Day Mode", fontWeight = FontWeight.Bold)
                    Text("Pause posture pings", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Switch(checked = isRestDay, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, checkedTrackColor = NeonGreen.copy(0.2f)))
        }
    }
}

@Composable
fun ConfettiOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.6f)).clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(120.dp).graphicsLayer(scaleX = scale, scaleY = scale))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "LEVEL UP!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = "You're getting stronger!", color = TextSecondary)
        }
    }
}

@Composable
fun PainLogDialog(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    var backPain by remember { mutableFloatStateOf(0f) }
    var sciaticPain by remember { mutableFloatStateOf(0f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Log Pain Levels", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Back Pain: ${backPain.toInt()}", color = TextSecondary)
                Slider(value = backPain, onValueChange = { backPain = it }, valueRange = 0f..10f, steps = 9)
                Text("Sciatic Pain: ${sciaticPain.toInt()}", color = TextSecondary)
                Slider(value = sciaticPain, onValueChange = { sciaticPain = it }, valueRange = 0f..10f, steps = 9)
            }
        },
        confirmButton = { Button(onClick = { onConfirm(backPain.toInt(), sciaticPain.toInt()) }) { Text("Save") } }
    )
}

@Composable
fun WeightLogDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Log Weight", color = TextPrimary) },
        text = {
            OutlinedTextField(value = weightInput, onValueChange = { weightInput = it }, label = { Text("Weight (kg)") })
        },
        confirmButton = { Button(onClick = { weightInput.toFloatOrNull()?.let { onConfirm(it) } }) { Text("Log") } }
    )
}
