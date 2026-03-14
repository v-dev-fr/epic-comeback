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

import com.recovery.back.domain.model.Exercise
import com.recovery.back.domain.model.ExerciseList

@Composable
fun ExercisesScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    dayNumber: Int = 4,
    showDirectionalCheck: Boolean = false, // W1D3 condition
    hasFlexionWarning: Boolean = false,
    contraindicationWarningVisible: Boolean = false
) {
    val exercises = ExerciseList.filter { it.phase == currentPhase }

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

        items(exercises.size) { index ->
            ExerciseCard(exercise = exercises[index])
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
fun ExerciseCard(exercise: Exercise) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            
            exercise.cue?.let {
                Text(
                    text = "Cue: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Text(
                text = "Reps: ${exercise.sets} x ${exercise.reps}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
