package com.recovery.back.presentation.screens.exercises

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recovery.back.domain.model.Exercise
import com.recovery.back.domain.model.ExerciseList
import com.recovery.back.presentation.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ExercisesScreen(
    currentPhase: Int = 1,
    weekNumber: Int = 1,
    dayNumber: Int = 4,
    showDirectionalCheck: Boolean = false,
    hasFlexionWarning: Boolean = false,
    contraindicationWarningVisible: Boolean = false,
    onExerciseComplete: (String, Int, Int) -> Unit = { _, _, _ -> }
) {
    val exercises = ExerciseList.filter { it.phase == currentPhase }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Today's Routine",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Phase $currentPhase • Focus: Spine Hygiene",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (hasFlexionWarning || contraindicationWarningVisible) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (hasFlexionWarning) {
                        WarningBanner("Flexion Intolerance Detected. Avoid forward bending.")
                    }
                    if (contraindicationWarningVisible) {
                        WarningBanner("Pain Spike Alert. Scale back intensity today.")
                    }
                }
            }
        }

        if (showDirectionalCheck) {
            item { DirectionalPreferenceCard() }
        }

        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onComplete = { onExerciseComplete(exercise.id, exercise.reps.toIntOrNull() ?: 10, currentPhase) }
            )
        }
        
        if (currentPhase == 4) {
            item {
                Button(
                    onClick = { /* Open Custom Exercise Dialog */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.05f))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Custom Phase 4 Lift", color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onComplete: () -> Unit) {
    var isDone by remember { mutableStateOf(false) }
    
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(
                    onClick = { 
                        isDone = !isDone
                        if (isDone) onComplete() 
                    },
                    modifier = Modifier.clip(CircleShape).background(if (isDone) NeonGreen.copy(0.1f) else Color.White.copy(0.05f))
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckBoxOutlineBlank, 
                        contentDescription = null, 
                        tint = if (isDone) NeonGreen else TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            exercise.cue?.let {
                Surface(
                    color = ElectricBlue.copy(0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricBlue,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatBadge(label = "Sets", value = exercise.sets)
                StatBadge(label = "Reps", value = exercise.reps)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun WarningBanner(message: String) {
    Surface(
        color = ErrorRed.copy(0.1f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ErrorRed.copy(0.2f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DirectionalPreferenceCard() {
    Surface(
        color = NeonGreen.copy(0.05f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonGreen.copy(0.1f), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Assessment Required",
                fontWeight = FontWeight.ExtraBold,
                color = NeonGreen,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Did your leg pain move toward your spine after the press-ups?",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Save */ },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Obsidian),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Centralized (Feeling Better)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
