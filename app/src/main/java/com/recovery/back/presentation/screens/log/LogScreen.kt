package com.recovery.back.presentation.screens.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var backPain by remember { mutableStateOf(0f) }
    var sciatica by remember { mutableStateOf(0f) }
    var waterGlasses by remember { mutableStateOf(0) }
    
    // Empty state logic: If nothing logged today, prompt user
    // Implied by DB state, but UI will always show the fresh sliders here if empty.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Daily Journal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Pain Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pain Levels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Back Pain: ${backPain.toInt()}/10")
                Slider(
                    value = backPain,
                    onValueChange = { backPain = it },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Sciatica / Leg Pain: ${sciatica.toInt()}/10")
                Slider(
                    value = sciatica,
                    onValueChange = { sciatica = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
            }
        }

        // Hydration Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hydration (Min: 8)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { if (waterGlasses > 0) waterGlasses-- }) { Text("-") }
                    Text("$waterGlasses Glasses", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = { waterGlasses++ }) { Text("+") }
                }
            }
        }

        // Nutrition / Supplements placeholder
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Supplements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    var taken by remember { mutableStateOf(false) }
                    Checkbox(checked = taken, onCheckedChange = { taken = it })
                    Text("Omega-3 & Vitamin D")
                }
            }
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Journal (+50 XP)")
        }
    }
}
