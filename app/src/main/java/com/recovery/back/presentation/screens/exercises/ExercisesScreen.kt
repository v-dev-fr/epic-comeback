package com.recovery.back.presentation.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExercisesScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    dayNumber: Int = 4,
    showDirectionalCheck: Boolean = false, // W1D3 condition
    hasFlexionWarning: Boolean = false,
    contraindicationWarningVisible: Boolean = false
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warnings List
        if (hasFlexionWarning) {
            item {
                WarningBanner(
                    message = "Your symptoms may not respond to this plan — consult a physiotherapist."
                )
            }
        }

        if (contraindicationWarningVisible) {
            item {
                WarningBanner(
                    message = "Your last session increased pain significantly. Consider pausing this exercise and consulting your PT."
                )
            }
        }

        if (showDirectionalCheck) {
            item {
                DirectionalPreferenceCard()
            }
        }

        item {
            Text(
                text = "Phase $currentPhase Exercises",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Mock Exercise List
        val exercises = when (currentPhase) {
            1 -> listOf("McKenzie Prone Lying", "Prone Press-Up (Cobra)", "Short Walks")
            2 -> listOf("McGill Curl-Up", "Side Bridge (knees)", "Bird-Dog", "Walks 20-30 min")
            3 -> listOf("Side Bridge (feet)", "Cat-Cow", "Glute Bridge", "Brisk Walk/Swim")
            4 -> listOf("McGill Big 3", "Glute Bridge", "Cat-Cow", "Brisk Walk (30-45m)")
            else -> emptyList()
        }

        items(exercises.size) { index ->
            ExerciseCard(name = exercises[index])
        }
        
        if (currentPhase == 4) {
            item {
                Button(
                    onClick = { /* Open Custom Exercise Dialog */ },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Add Custom Phase 4 Exercise")
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(name: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            if (name.contains("McGill")) {
                Text(
                    text = "Cue: Breathe behind the shield — maintain brace. DO NOT hold breath.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(text = "Reverse Pyramid: 8 -> 6 -> 4 reps", style = MaterialTheme.typography.bodyMedium)
            // Gamification: Timer button, complete button triggering haptics/confetti
        }
    }
}

@Composable
fun WarningBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DirectionalPreferenceCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Day 3 Check-In", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Text("After doing your Cobra press-ups, does your leg/buttock pain:")
            
            // Radio buttons would go here for:
            // (a) Move toward spine
            // (b) Stay the same
            // (c) Move further down leg
            
            Button(onClick = { /* Save to UserProfile */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Save Observation")
            }
        }
    }
}
