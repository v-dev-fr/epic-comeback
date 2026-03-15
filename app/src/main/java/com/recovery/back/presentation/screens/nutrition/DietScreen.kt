package com.recovery.back.presentation.screens.nutrition

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
import com.recovery.back.presentation.ui.theme.*

import com.recovery.back.data.local.entity.MealLogEntity

@Composable
fun DietScreen(
    meals: List<MealLogEntity> = emptyList(),
    onFoodGuideClick: () -> Unit,
    onAddMealClick: (String, Int) -> Unit
) {
    var showAddMealDialog by remember { mutableStateOf(false) }

    if (showAddMealDialog) {
        AddMealDialog(
            onDismiss = { showAddMealDialog = false },
            onConfirm = { name, cals ->
                onAddMealClick(name, cals)
                showAddMealDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        item {
            Text(
                text = "Nutrition Hub",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "IBS-Friendly recovery fuel",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            // Safe Food Guide Quick Access
            Surface(
                onClick = onFoodGuideClick,
                color = ElectricBlue.copy(0.1f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElectricBlue.copy(0.2f), RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Obsidian)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text("Food Reference", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("What can I eat today?", style = MaterialTheme.typography.bodySmall, color = ElectricBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Today's Meals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { showAddMealDialog = true },
                    modifier = Modifier.background(NeonGreen, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Obsidian)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (meals.isEmpty()) {
            item { EmptyMealState() }
        } else {
            items(meals) { meal ->
                MealItem(meal)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            SupplementCard()
        }
    }
}

@Composable
fun MealItem(meal: MealLogEntity) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(meal.mealName, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${meal.calories} kcal", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Icon(Icons.Default.Restaurant, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AddMealDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Log Meal", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories (approx)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, calories.toIntOrNull() ?: 0) }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Obsidian)) {
                Text("Log")
            }
        }
    )
}

@Composable
fun EmptyMealState() {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
            Text(
                "No meals logged. Tracking helps identify IBS triggers.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SupplementCard() {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Supplements", fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("Magnesium Citrate", color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("Probiotic (VSL#3)", color = TextSecondary)
            }
        }
    }
}
