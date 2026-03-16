package com.recovery.back.presentation.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import com.recovery.back.presentation.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.recovery.back.data.local.entity.WeightLogEntity

@Composable
fun ProgressScreen(
    weightLogs: List<WeightLogEntity> = emptyList(),
    painCorrelation: List<Pair<Float, Float>> = emptyList(),
    consistencyScore: Float = 0f,
    onExportClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Obsidian).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "Recovery Trends", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(text = "Consistency: ${(consistencyScore * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = NeonGreen)
            }
            IconButton(onClick = onExportClick, modifier = Modifier.background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(12.dp))) {
                Icon(Icons.Default.Share, contentDescription = "Export")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (weightLogs.isEmpty() && painCorrelation.isEmpty()) {
            Surface(color = SurfaceDark, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Text(text = "Unlock deep insights after your first routine.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        } else {
            // Weight Chart
            TrendCard("Weight Trend (kg)") {
                val weightEntries = weightLogs.mapIndexed { index, log -> index.toFloat() to log.weightKg }
                if (weightEntries.isNotEmpty()) {
                    Chart(chart = lineChart(), model = entryModelOf(weightEntries.map { it.second }), startAxis = rememberStartAxis(), bottomAxis = rememberBottomAxis(), modifier = Modifier.fillMaxSize())
                } else {
                    Text("No weight data yet", color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pain Correlation Chart
            TrendCard("Pain vs consistency") {
                val painEntries = painCorrelation.map { it.first }
                if (painEntries.isNotEmpty()) {
                    Chart(chart = columnChart(), model = entryModelOf(painEntries), startAxis = rememberStartAxis(), bottomAxis = rememberBottomAxis(), modifier = Modifier.fillMaxSize())
                } else {
                    Text("No pain logs yet", color = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Achievement Gallery Preview
            AchievementGallery()
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TrendCard(title: String, content: @Composable () -> Unit) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                content()
            }
        }
    }
}

@Composable
fun AchievementGallery() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Badge(label = "7-Day Streak", color = StreakFlame)
            Badge(label = "Hydrated", color = Color(0xFF63B3ED))
            Badge(label = "Early Bird", color = XpGold)
        }
    }
}

@Composable
fun Badge(label: String, color: Color) {
    Surface(color = color.copy(0.1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, color.copy(0.2f))) {
        Text(text = label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = color, style = MaterialTheme.typography.labelSmall)
    }
}
