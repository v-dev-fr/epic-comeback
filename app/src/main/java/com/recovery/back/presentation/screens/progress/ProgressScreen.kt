package com.recovery.back.presentation.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProgressScreen(
    hasData: Boolean = false,
    onExportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onExportClick) {
                Text("Export PDF")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!hasData) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data yet — complete today's check-in to start your trend.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Dual-Axis Chart Mockup (Pain vs IBS)
            Card(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "IBS & Back Pain Correlation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("[ Dual-Axis Chart Rendered Here ]", color = MaterialTheme.colorScheme.primary)
                        // In a real implementation this would use Vico's Chart composable
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weight Chart Mockup
            Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weight Trend (kg)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("[ Weight Line Chart ]")
                    }
                }
            }
        }
    }
}
